using System.Collections.Generic;
using Newtonsoft.Json;

namespace agrisense.api.DTOs
{
    public class FertilizerRecommendationRequest
    {
        [JsonProperty("farmId")] public int FarmId { get; set; }
        [JsonProperty("crop")] public string Crop { get; set; }
        [JsonProperty("soilType")] public string SoilType { get; set; }
    }

    public class FertilizerAiRequest
    {
        [JsonProperty("crop")] public string Crop { get; set; }
        [JsonProperty("soil_type")] public string SoilType { get; set; }
        [JsonProperty("nitrogen")] public double Nitrogen { get; set; }
        [JsonProperty("phosphorus")] public double Phosphorus { get; set; }
        [JsonProperty("potassium")] public double Potassium { get; set; }
        [JsonProperty("temperature")] public double Temperature { get; set; }
        [JsonProperty("humidity")] public double Humidity { get; set; }
        [JsonProperty("ph")] public double Ph { get; set; }
        [JsonProperty("rainfall")] public double Rainfall { get; set; }
    }

    public class FertilizerRecommendationResponse
    {
        [JsonProperty("success")] public bool Success { get; set; }
        [JsonProperty("recommendedFertilizer")] public string RecommendedFertilizer { get; set; }
        [JsonProperty("recommendations")] public List<FertilizerClassPrediction> Recommendations { get; set; }
        [JsonProperty("input")] public FertilizerAiRequest Input { get; set; }
        [JsonProperty("note")] public string Note { get; set; }
    }

    public class FertilizerClassPrediction
    {
        [JsonProperty("fertilizer")] public string Fertilizer { get; set; }
        [JsonProperty("confidence")] public double Confidence { get; set; }
        [JsonProperty("confidencePercentage")] public double ConfidencePercentage { get; set; }
    }

    public class FertilizerModelOptions
    {
        [JsonProperty("success")] public bool Success { get; set; }
        [JsonProperty("crops")] public List<string> Crops { get; set; }
        [JsonProperty("soilTypes")] public List<string> SoilTypes { get; set; }
        [JsonProperty("fertilizers")] public List<string> Fertilizers { get; set; }
        [JsonProperty("inputFields")] public List<string> InputFields { get; set; }
    }
}
