using System;
using System.Threading.Tasks;
using System.Web.Http;
using agrisense.api.DTOs;
using agrisense.api.Services;

namespace agrisense.api.Controllers
{
    [RoutePrefix("api/recommendations")]
    [FirebaseAuthorize]
    public class CropRecommendationController : ApiController
    {
        private readonly CropAiServices _cropAiServices;
        private readonly AgriSenseRepository _repository = new AgriSenseRepository();


        public CropRecommendationController()
        {
            _cropAiServices =
                new CropAiServices();
        }


        [HttpPost]
        [Route("crop")]
        public async Task<IHttpActionResult>
            RecommendCrop(
                CropRecommendationRequest request)
        {
            try
            {
                if (request == null)
                {
                    return BadRequest(
                        "Request body is required."
                    );
                }

                string error = Validate(request);
                if (error != null) return BadRequest(error);


                CropRecommendationResponse result =
                    await _cropAiServices
                        .PredictCropAsync(request);


                return Ok(result);
            }
            catch (Exception ex)
            {
                return InternalServerError(ex);
            }
        }

        private static string Validate(CropRecommendationRequest r)
        {
            if (Double.IsNaN(r.N) || Double.IsInfinity(r.N) || Double.IsNaN(r.P) || Double.IsInfinity(r.P) || Double.IsNaN(r.K) || Double.IsInfinity(r.K) || Double.IsNaN(r.Temperature) || Double.IsInfinity(r.Temperature) || Double.IsNaN(r.Humidity) || Double.IsInfinity(r.Humidity) || Double.IsNaN(r.Ph) || Double.IsInfinity(r.Ph) || Double.IsNaN(r.Rainfall) || Double.IsInfinity(r.Rainfall)) return "All seven model inputs must be finite numbers.";
            if (r.N < 0 || r.P < 0 || r.K < 0 || r.Temperature < -30 || r.Temperature > 70 || r.Humidity < 0 || r.Humidity > 100 || r.Ph < 0 || r.Ph > 14 || r.Rainfall < 0) return "Crop model inputs are outside the supported range.";
            return null;
        }

        [HttpGet]
        [Route("crop/{farmId:int}")]
        public async Task<IHttpActionResult> RecommendLatest(int farmId)
        {
            try
            {
                var userId = FirebaseUserContext.EnsureDatabaseUser(User, _repository);
                if (_repository.GetFarm(farmId, userId) == null) return NotFound();
                var reading = _repository.GetLatest(farmId, userId);
                if (reading == null) return NotFound();
                var request = new CropRecommendationRequest { N=reading.N, P=reading.P, K=reading.K, Temperature=reading.Temperature, Humidity=reading.Humidity, Ph=reading.Ph, Rainfall=reading.Rainfall };
                var result = await _cropAiServices.PredictCropAsync(request);
                _repository.SaveCropRecommendation(farmId, reading.ReadingId, result);
                return Ok(result);
            }
            catch (Exception ex) { return InternalServerError(ex); }
        }
    }
}
