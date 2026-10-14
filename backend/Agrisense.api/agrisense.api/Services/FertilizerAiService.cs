using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using agrisense.api.DTOs;

namespace agrisense.api.Services
{
    public class FertilizerAiService
    {
        private readonly HttpClient _httpClient;

        public FertilizerAiService()
        {
            var baseUrl = Environment.GetEnvironmentVariable("FERTILIZER_AI_BASE_URL");
            if (String.IsNullOrWhiteSpace(baseUrl)) baseUrl = "http://127.0.0.1:8001/";
            _httpClient = new HttpClient { BaseAddress = new Uri(baseUrl.EndsWith("/") ? baseUrl : baseUrl + "/") };
            var serviceKey = Environment.GetEnvironmentVariable("AI_SERVICE_API_KEY");
            if (!String.IsNullOrWhiteSpace(serviceKey)) _httpClient.DefaultRequestHeaders.Add("X-Service-Key", serviceKey);
            _httpClient.Timeout = TimeSpan.FromSeconds(100);
        }

        public async Task<FertilizerRecommendationResponse> PredictAsync(FertilizerAiRequest request)
        {
            var content = new StringContent(JsonConvert.SerializeObject(request), Encoding.UTF8, "application/json");
            var response = await _httpClient.PostAsync("predict/fertilizer", content);
            var body = await response.Content.ReadAsStringAsync();
            if (!response.IsSuccessStatusCode) throw new HttpRequestException("Fertilizer AI returned HTTP " + (int)response.StatusCode + ": " + body);
            return JsonConvert.DeserializeObject<FertilizerRecommendationResponse>(body);
        }

        public async Task<FertilizerModelOptions> GetOptionsAsync()
        {
            var response = await _httpClient.GetAsync("metadata/fertilizer");
            var body = await response.Content.ReadAsStringAsync();
            if (!response.IsSuccessStatusCode) throw new HttpRequestException("Fertilizer AI returned HTTP " + (int)response.StatusCode + ": " + body);
            return JsonConvert.DeserializeObject<FertilizerModelOptions>(body);
        }
    }
}
