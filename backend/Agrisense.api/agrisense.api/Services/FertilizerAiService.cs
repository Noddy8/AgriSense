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
            _httpClient = new HttpClient { BaseAddress = new Uri("http://127.0.0.1:8001/") };
            _httpClient.Timeout = TimeSpan.FromSeconds(20);
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
