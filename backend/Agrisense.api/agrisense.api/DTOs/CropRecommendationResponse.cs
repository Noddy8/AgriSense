using System.Collections.Generic;
using Newtonsoft.Json;

namespace agrisense.api.DTOs
{
    public class CropRecommendationResponse
    {
        [JsonProperty("success")]
        public bool Success { get; set; }

        [JsonProperty("recommendedCrop")]
        public string RecommendedCrop { get; set; }

        [JsonProperty("recommendations")]
        public List<CropRecommendation> Recommendations { get; set; }

        [JsonProperty("input")]
        public CropInput Input { get; set; }
    }


    public class CropRecommendation
    {
        [JsonProperty("crop")]
        public string Crop { get; set; }

        [JsonProperty("confidence")]
        public double Confidence { get; set; }

        [JsonProperty("confidencePercentage")]
        public double ConfidencePercentage { get; set; }
    }


    public class CropInput
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