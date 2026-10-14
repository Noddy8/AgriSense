using System;
using System.Linq;
using System.Web.Http;
using agrisense.api.Models;
using agrisense.api.Services;

namespace agrisense.api.Controllers
{
    [RoutePrefix("api/sensors")]
    [FirebaseAuthorize]
    public class SensorsController : ApiController
    {
        private readonly AgriSenseRepository _repository = new AgriSenseRepository();
        private int UserId { get { return FirebaseUserContext.EnsureDatabaseUser(User, _repository); } }
        [HttpGet, Route("latest/{farmId:int}")] public IHttpActionResult Latest(int farmId) { try { var userId = UserId; if (_repository.GetFarm(farmId, userId) == null) return NotFound(); var reading = _repository.GetLatest(farmId, userId); return reading == null ? (IHttpActionResult)NotFound() : Ok(reading); } catch { return InternalServerError(); } }
        [HttpGet, Route("history/{farmId:int}")] public IHttpActionResult History(int farmId, int limit = 50) { try { var userId = UserId; if (_repository.GetFarm(farmId, userId) == null) return NotFound(); return Ok(_repository.GetHistory(farmId, userId, Math.Max(1, Math.Min(200, limit)))); } catch { return InternalServerError(); } }
        [HttpPost, Route("readings")] public IHttpActionResult Create(SensorReadingRequest request)
        {
            var error = Validate(request); if (error != null) return BadRequest(error);
            try { if (_repository.GetFarm(request.FarmId, UserId) == null) return NotFound(); request.Source = String.Equals(request.Source, "simulation", StringComparison.OrdinalIgnoreCase) ? "simulation" : "device"; return Ok(_repository.AddReading(request)); } catch { return InternalServerError(); }
        }
        [HttpPost, Route("simulation/{farmId:int}")] public IHttpActionResult Simulate(int farmId, string scenario = "normal")
        {
            int userId; try { userId = UserId; } catch { return Unauthorized(); }
            var farm = _repository.GetFarm(farmId, userId); if (farm == null) return NotFound();
            var reading = new SensorReadingRequest { FarmId=farmId, Source="simulation", N=90, P=42, K=43, Temperature=20.8, Humidity=82, Ph=6.5, Rainfall=202.9, SoilMoisture=42 };
            switch ((scenario ?? "normal").ToLowerInvariant())
            {
                case "normal": break;
                case "low-rainfall": reading.Rainfall=45; reading.SoilMoisture=24; break;
                case "high-temperature": reading.Temperature=37; reading.Humidity=51; break;
                case "low-nitrogen": reading.N=24; break;
                case "high-humidity": reading.Humidity=96; break;
                default: return BadRequest("Scenario must be normal, low-rainfall, high-temperature, low-nitrogen, or high-humidity.");
            }
            // Each tap produces a fresh, plausible reading around the selected scenario profile.
            var random = new Random(unchecked((int)DateTime.UtcNow.Ticks ^ Guid.NewGuid().GetHashCode()));
            reading.N = Vary(random, reading.N, 5, 0, 140);
            reading.P = Vary(random, reading.P, 5, 0, 145);
            reading.K = Vary(random, reading.K, 5, 0, 205);
            reading.Temperature = Vary(random, reading.Temperature, 1.5, -5, 50);
            reading.Humidity = Vary(random, reading.Humidity, 4, 0, 100);
            reading.Ph = Vary(random, reading.Ph, 0.2, 3, 10);
            reading.Rainfall = Vary(random, reading.Rainfall, 10, 0, 350);
            reading.SoilMoisture = Vary(random, reading.SoilMoisture.Value, 4, 0, 100);
            try { return Ok(_repository.AddReading(reading)); } catch { return InternalServerError(); }
        }
        private static double Vary(Random random, double center, double spread, double min, double max)
        {
            return Math.Round(Math.Max(min, Math.Min(max, center + (random.NextDouble() * 2 - 1) * spread)), 1);
        }
        private static string Validate(SensorReadingRequest r)
        {
            if (r == null) return "Request body is required.";
            if (r.FarmId < 1) return "A valid farmId is required.";
            if (Double.IsNaN(r.N) || Double.IsInfinity(r.N) || Double.IsNaN(r.P) || Double.IsInfinity(r.P) || Double.IsNaN(r.K) || Double.IsInfinity(r.K) || Double.IsNaN(r.Temperature) || Double.IsInfinity(r.Temperature) || Double.IsNaN(r.Humidity) || Double.IsInfinity(r.Humidity) || Double.IsNaN(r.Ph) || Double.IsInfinity(r.Ph) || Double.IsNaN(r.Rainfall) || Double.IsInfinity(r.Rainfall) || r.N < 0 || r.P < 0 || r.K < 0 || r.Temperature < -30 || r.Temperature > 70 || r.Humidity < 0 || r.Humidity > 100 || r.Ph < 0 || r.Ph > 14 || r.Rainfall < 0 || (r.SoilMoisture.HasValue && (r.SoilMoisture.Value < 0 || r.SoilMoisture.Value > 100))) return "Sensor values are outside the supported range.";
            return null;
        }
    }
}
