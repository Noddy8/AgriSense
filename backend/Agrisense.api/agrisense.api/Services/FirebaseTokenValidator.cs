using System;
using System.Collections.Generic;
using System.Net;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using Newtonsoft.Json.Linq;

namespace agrisense.api.Services
{
    // Validates Firebase ID tokens against Google's rotating Secure Token certificates.
    // No service-account key is required or stored by this API.
    internal static class FirebaseTokenValidator
    {
        private const string CertificateUrl = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
        private static readonly object CacheLock = new object();
        private static Dictionary<string, X509Certificate2> _certificates = new Dictionary<string, X509Certificate2>();
        private static DateTime _certificatesExpireAtUtc = DateTime.MinValue;
        private static DateTime _lastCertificateRefreshUtc = DateTime.MinValue;

        public static JObject Validate(string token)
        {
            if (String.IsNullOrWhiteSpace(token) || token.Length > 16384) throw new InvalidOperationException("Missing or invalid Firebase token.");
            var parts = token.Split('.');
            if (parts.Length != 3) throw new InvalidOperationException("Malformed Firebase token.");

            var header = JObject.Parse(DecodeText(parts[0]));
            if ((string)header["alg"] != "RS256") throw new InvalidOperationException("Unsupported token signing algorithm.");
            var keyId = (string)header["kid"];
            var certificate = GetCertificate(keyId);
            var signingInput = Encoding.ASCII.GetBytes(parts[0] + "." + parts[1]);
            using (certificate)
            using (var rsa = certificate.PublicKey.Key as RSA)
            {
                if (rsa == null || !rsa.VerifyData(
                    signingInput,
                    DecodeBytes(parts[2]),
                    HashAlgorithmName.SHA256,
                    RSASignaturePadding.Pkcs1))
                    throw new InvalidOperationException("Firebase token signature is invalid.");
            }

            var claims = JObject.Parse(DecodeText(parts[1]));
            var projectId = FirebaseProjectId;
            var now = (long)(DateTime.UtcNow - new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc)).TotalSeconds;
            var exp = (long?)claims["exp"] ?? 0;
            var issuedAt = (long?)claims["iat"] ?? 0;
            var authTime = (long?)claims["auth_time"] ?? 0;
            var uid = (string)claims["sub"];

            if ((string)claims["aud"] != projectId || (string)claims["iss"] != "https://securetoken.google.com/" + projectId ||
                String.IsNullOrWhiteSpace(uid) || uid.Length > 128 || exp <= now || issuedAt > now + 60 || authTime > now + 60 || authTime <= 0)
                throw new InvalidOperationException("Firebase token claims are invalid or expired.");

            return claims;
        }

        private static X509Certificate2 GetCertificate(string keyId)
        {
            if (String.IsNullOrWhiteSpace(keyId)) throw new InvalidOperationException("Firebase token has no signing key id.");
            lock (CacheLock)
            {
                if (_certificatesExpireAtUtc <= DateTime.UtcNow ||
                    (!_certificates.ContainsKey(keyId) && DateTime.UtcNow - _lastCertificateRefreshUtc > TimeSpan.FromSeconds(60))) RefreshCertificates();
                X509Certificate2 certificate;
                if (!_certificates.TryGetValue(keyId, out certificate)) throw new InvalidOperationException("Firebase signing key is unknown.");
                // Work from a private copy so RefreshCertificates can safely dispose cached
                // certificates while other requests are validating tokens.
                return new X509Certificate2(certificate.RawData);
            }
        }

        private static void RefreshCertificates()
        {
            ServicePointManager.SecurityProtocol |= SecurityProtocolType.Tls12;
            var request = (HttpWebRequest)WebRequest.Create(CertificateUrl);
            request.Method = "GET";
            request.Timeout = 8000;
            request.ReadWriteTimeout = 8000;
            using (var response = (HttpWebResponse)request.GetResponse())
            using (var stream = response.GetResponseStream())
            using (var reader = new System.IO.StreamReader(stream))
            {
                var json = JObject.Parse(reader.ReadToEnd());
                var refreshed = new Dictionary<string, X509Certificate2>();
                foreach (var property in json.Properties())
                {
                    var pem = (string)property.Value;
                    var base64 = pem.Replace("-----BEGIN CERTIFICATE-----", "").Replace("-----END CERTIFICATE-----", "").Replace("\r", "").Replace("\n", "").Trim();
                    refreshed[property.Name] = new X509Certificate2(Convert.FromBase64String(base64));
                }
                foreach (var oldCertificate in _certificates.Values) oldCertificate.Dispose();
                _certificates = refreshed;
                _lastCertificateRefreshUtc = DateTime.UtcNow;
                var maxAge = 3600;
                var cacheControl = response.Headers[HttpResponseHeader.CacheControl];
                if (!String.IsNullOrEmpty(cacheControl))
                    foreach (var directive in cacheControl.Split(','))
                        if (directive.Trim().StartsWith("max-age=", StringComparison.OrdinalIgnoreCase))
                        {
                            int parsed;
                            if (Int32.TryParse(directive.Trim().Substring(8), out parsed)) maxAge = Math.Max(60, Math.Min(parsed, 86400));
                        }
                _certificatesExpireAtUtc = DateTime.UtcNow.AddSeconds(maxAge);
            }
        }

        private static string DecodeText(string segment) { return Encoding.UTF8.GetString(DecodeBytes(segment)); }
        private static byte[] DecodeBytes(string segment)
        {
            var value = segment.Replace('-', '+').Replace('_', '/');
            switch (value.Length % 4) { case 2: value += "=="; break; case 3: value += "="; break; }
            return Convert.FromBase64String(value);
        }

        private static string FirebaseProjectId
        {
            get
            {
                var value = System.Configuration.ConfigurationManager.AppSettings["FirebaseProjectId"];
                if (String.IsNullOrWhiteSpace(value)) throw new InvalidOperationException("FirebaseProjectId is not configured.");
                return value.Trim();
            }
        }
    }
}
