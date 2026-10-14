using System;
using System.Diagnostics;
using System.Net;
using System.Security.Claims;
using System.Security.Principal;
using System.Web.Http.Controllers;
using System.Web.Http.Filters;
using System.Net.Http;

namespace agrisense.api.Services
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method, AllowMultiple = false)]
    public sealed class FirebaseAuthorizeAttribute : AuthorizationFilterAttribute
    {
        public override void OnAuthorization(HttpActionContext actionContext)
        {
            var authorization = actionContext.Request.Headers.Authorization;
            if (authorization == null || !String.Equals(authorization.Scheme, "Bearer", StringComparison.OrdinalIgnoreCase))
            {
                Reject(actionContext, "Sign in to continue.");
                return;
            }

            try
            {
                var firebaseClaims = FirebaseTokenValidator.Validate(authorization.Parameter);
                var identity = new ClaimsIdentity("Firebase");
                identity.AddClaim(new Claim(ClaimTypes.NameIdentifier, (string)firebaseClaims["sub"]));
                AddClaim(identity, "email", (string)firebaseClaims["email"]);
                identity.AddClaim(new Claim("email_verified", ((bool?)firebaseClaims["email_verified"] ?? false).ToString()));
                AddClaim(identity, "name", (string)firebaseClaims["name"]);
                AddClaim(identity, "phone_number", (string)firebaseClaims["phone_number"]);
                var principal = new ClaimsPrincipal(identity);
                actionContext.RequestContext.Principal = principal;
            }
            catch (WebException)
            {
                actionContext.Response = actionContext.Request.CreateResponse(HttpStatusCode.ServiceUnavailable,
                    new { message = "The API cannot verify Firebase sign-in right now. Check the server's internet connection and retry." });
            }
            catch (Exception exception)
            {
                Trace.TraceWarning("Firebase token validation rejected a request: {0}", exception);
                Reject(actionContext, "Firebase rejected the sign-in token. Sign out and sign in again; if it continues, verify that the API and Android app use the same Firebase project.");
            }
        }

        private static void AddClaim(ClaimsIdentity identity, string type, string value)
        {
            if (!String.IsNullOrWhiteSpace(value)) identity.AddClaim(new Claim(type, value));
        }

        private static void Reject(HttpActionContext context, string message)
        {
            context.Response = context.Request.CreateResponse(HttpStatusCode.Unauthorized, new { message = message });
        }
    }
}
