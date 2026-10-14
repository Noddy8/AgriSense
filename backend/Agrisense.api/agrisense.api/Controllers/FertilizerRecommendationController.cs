using System;
using System.Web.Http;
using agrisense.api.DTOs;
using agrisense.api.Services;

namespace agrisense.api.Controllers
{
    [RoutePrefix("api/recommendations/fertilizer")]
    [FirebaseAuthorize]
    public class FertilizerRecommendationController : ApiController
    {
        private readonly AgriSenseRepository _repository = new AgriSenseRepository();
        private readonly FertilizerAiService _fertilizerAi = new FertilizerAiService();

        [HttpGet, Route("options")]
        public async System.Threading.Tasks.Task<IHttpActionResult> Options()
        {
            try { return Ok(await _fertilizerAi.GetOptionsAsync()); }
            catch (System.Net.Http.HttpRequestException ex) { return Content((System.Net.HttpStatusCode)502, new { message = ex.Message }); }
            catch (Exception ex) { return InternalServerError(ex); }
        }

        [HttpPost, Route("")]
        public async System.Threading.Tasks.Task<IHttpActionResult> Recommend(FertilizerRecommendationRequest request)
        {
            if (request == null || request.FarmId < 1 || String.IsNullOrWhiteSpace(request.Crop)) return BadRequest("farmId and crop are required.");
            try
            {
                var userId = FirebaseUserContext.EnsureDatabaseUser(User, _repository);
                var farm = _repository.GetFarm(request.FarmId, userId);
                if (farm == null) return NotFound();
                var soilType = String.IsNullOrWhiteSpace(request.SoilType) ? farm.SoilType : request.SoilType;
                if (String.IsNullOrWhiteSpace(soilType)) return BadRequest("Add or select a soil type before requesting fertilizer advice.");
                var reading = _repository.GetLatest(request.FarmId, userId);
                if (reading == null) return BadRequest("No sensor reading is available for this farm yet.");
                var input = new FertilizerAiRequest { Crop=request.Crop, SoilType=soilType, Nitrogen=reading.N, Phosphorus=reading.P, Potassium=reading.K, Temperature=reading.Temperature, Humidity=reading.Humidity, Ph=reading.Ph, Rainfall=reading.Rainfall };
                var result = await _fertilizerAi.PredictAsync(input);
                _repository.SaveFertilizerRecommendation(request.FarmId, result);
                return Ok(result);
            }
            catch (System.Net.Http.HttpRequestException ex) { return Content((System.Net.HttpStatusCode)502, new { message = ex.Message }); }
            catch (Exception ex) { return InternalServerError(ex); }
        }
    }
}
