using System;
using System.Net;
using System.Web.Http;
using agrisense.api.Models;
using agrisense.api.Services;

namespace agrisense.api.Controllers
{
    [RoutePrefix("api/farms")]
    [FirebaseAuthorize]
    public class FarmsController : ApiController
    {
        private readonly AgriSenseRepository _repository = new AgriSenseRepository();
        private int UserId { get { return FirebaseUserContext.EnsureDatabaseUser(User, _repository); } }
        [HttpGet, Route("")] public IHttpActionResult Get()
        {
            try { var userId = UserId; _repository.EnsureDefaultFarm(userId, null); return Ok(_repository.GetFarms(userId)); }
            catch
            {
                return Content(HttpStatusCode.InternalServerError, new
                {
                    message = "Could not load farms. Check the AgriSense SQL connection and apply database migrations 001_initial_schema.sql and 002_firebase_auth.sql."
                });
            }
        }
        [HttpGet, Route("{id:int}")] public IHttpActionResult Get(int id) { try { var farm = _repository.GetFarm(id, UserId); return farm == null ? (IHttpActionResult)NotFound() : Ok(farm); } catch { return InternalServerError(); } }
        [HttpPost, Route("")] public IHttpActionResult Create(FarmCreateRequest request)
        {
            if (request == null || String.IsNullOrWhiteSpace(request.Name) || request.AreaHectares < 0) return BadRequest("A farm name and non-negative area are required.");
            try { return Content(HttpStatusCode.Created, _repository.CreateFarm(request, UserId)); } catch { return InternalServerError(); }
        }
        [HttpGet, Route("{id:int}/dashboard")] public IHttpActionResult Dashboard(int id)
        {
            try { var userId = UserId; var farm = _repository.GetFarm(id, userId); if (farm == null) return NotFound(); var reading = _repository.GetLatest(id, userId); return Ok(new DashboardSummary { Farm=farm, LatestReading=reading, DataMode=reading == null ? "No readings" : reading.Source }); }
            catch { return InternalServerError(); }
        }
    }
}
