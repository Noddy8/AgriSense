using Newtonsoft.Json;

namespace agrisense.api.DTOs
{
    public class CropRecommendationRequest
    {
        [JsonProperty("N")]
        public double N { get; set; }

        [JsonProperty("P")]
        public double P { get; set; }

        [JsonProperty("K")]
        public double K { get; set; }

        [JsonProperty("temperature")]
        public double Temperature { get; set; }

        [JsonProperty("humidity")]
        public double Humidity { get; set; }

        [JsonProperty("ph")]
        public double Ph { get; set; }

        [JsonProperty("rainfall")]
        public double Rainfall { get; set; }
    }
}