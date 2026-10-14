using System;
using System.Web.Http;
using agrisense.api.Models;
using agrisense.api.Services;

namespace agrisense.api.Controllers
{
    [FirebaseAuthorize]
    [RoutePrefix("api/account")]
    public class AccountController : ApiController
    {
        private readonly AgriSenseRepository _repository = new AgriSenseRepository();

        [HttpPost, Route("bootstrap")]
        public IHttpActionResult Bootstrap(AccountBootstrapRequest request)
        {
            if (request == null) request = new AccountBootstrapRequest();
            if (request.FullName != null && request.FullName.Length > 120) return BadRequest("Name is too long.");
            if (request.FarmName != null && request.FarmName.Length > 120) return BadRequest("Farm name is too long.");
            if (request.MobileNumber != null && request.MobileNumber.Length > 32) return BadRequest("Mobile number is too long.");
            try
            {
                var userId = FirebaseUserContext.EnsureDatabaseUser(User, _repository, request.FullName, request.MobileNumber);
                var farm = _repository.EnsureDefaultFarm(userId, request.FarmName);
                return Ok(new { userId = userId, farmId = farm.FarmId, farmName = farm.Name });
            }
            catch (Exception ex) { return InternalServerError(ex); }
        }
    }
}
