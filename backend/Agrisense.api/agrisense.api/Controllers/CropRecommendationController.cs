using System;
using System.Threading.Tasks;
using System.Web.Http;
using agrisense.api.DTOs;
using agrisense.api.Services;

namespace agrisense.api.Controllers
{
    [RoutePrefix("api/recommendations")]
    public class CropRecommendationController : ApiController
    {
        private readonly CropAiServices _cropAiServices;


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
    }
}