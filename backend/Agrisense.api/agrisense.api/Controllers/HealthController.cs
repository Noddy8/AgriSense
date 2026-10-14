using System.Web.Http;

namespace agrisense.api.Controllers
{
    [RoutePrefix("api/health")]
    public class HealthController : ApiController
    {
        [HttpGet, Route("")]
        public IHttpActionResult Get()
        {
            return Ok(new { status = "running", service = "AgriSense API", mode = "prototype" });
        }
    }
}
