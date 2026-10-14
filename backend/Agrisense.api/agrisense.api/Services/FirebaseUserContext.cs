using System;
using System.Security.Claims;
using System.Security.Principal;

namespace agrisense.api.Services
{
    public static class FirebaseUserContext
    {
        public static int EnsureDatabaseUser(IPrincipal principal, AgriSenseRepository repository, string profileName = null, string mobileNumber = null)
        {
            var identity = principal == null ? null : principal.Identity as ClaimsIdentity;
            var uid = identity == null ? null : identity.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if (String.IsNullOrWhiteSpace(uid)) throw new UnauthorizedAccessException("Firebase identity is missing.");
            var email = identity.FindFirst("email")?.Value;
            bool emailVerified;
            Boolean.TryParse(identity.FindFirst("email_verified")?.Value, out emailVerified);
            var name = String.IsNullOrWhiteSpace(profileName) ? identity.FindFirst("name")?.Value : profileName;
            var phone = String.IsNullOrWhiteSpace(mobileNumber) ? identity.FindFirst("phone_number")?.Value : mobileNumber;
            return repository.GetOrCreateFirebaseUserId(uid, email, emailVerified, name, phone);
        }
    }
}
