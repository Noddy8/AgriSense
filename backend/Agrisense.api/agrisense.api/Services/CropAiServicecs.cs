using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using agrisense.api.DTOs;

namespace agrisense.api.Services
{
    public class CropAiServices
    {
        private readonly HttpClient _httpClient;

        public CropAiServices()
        {
            _httpClient = new HttpClient();

            _httpClient.BaseAddress =
                new Uri("http://127.0.0.1:8000/");
        }


        public async Task<CropRecommendationResponse>
            PredictCropAsync(
                CropRecommendationRequest request)
        {
            string json =
                JsonConvert.SerializeObject(request);


            var content =
                new StringContent(
                    json,
                    Encoding.UTF8,
                    "application/json"
                );


            HttpResponseMessage response =
                await _httpClient.PostAsync(
                    "predict/crop",
                    content
                );


            string responseContent =
                await response.Content.ReadAsStringAsync();


            if (!response.IsSuccessStatusCode)
            {
                throw new Exception(
                    "Python AI Service returned error: "
                    + responseContent
                );
            }


            CropRecommendationResponse result =
                JsonConvert.DeserializeObject
                <CropRecommendationResponse>(
                    responseContent
                );


            return result;
        }
    }
}