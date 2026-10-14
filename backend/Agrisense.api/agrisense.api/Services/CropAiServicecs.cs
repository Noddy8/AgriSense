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
            var baseUrl = Environment.GetEnvironmentVariable("CROP_AI_BASE_URL");
            if (String.IsNullOrWhiteSpace(baseUrl)) baseUrl = "http://127.0.0.1:8000/";
            _httpClient.BaseAddress = new Uri(baseUrl.EndsWith("/") ? baseUrl : baseUrl + "/");
            var serviceKey = Environment.GetEnvironmentVariable("AI_SERVICE_API_KEY");
            if (!String.IsNullOrWhiteSpace(serviceKey)) _httpClient.DefaultRequestHeaders.Add("X-Service-Key", serviceKey);
            _httpClient.Timeout = TimeSpan.FromSeconds(100);
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
