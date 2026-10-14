package com.ninjaTurtles.agrisense.models;

public class FertilizerPredictionRequest {
    public int farmId;
    public String crop;
    public String soilType;
    public FertilizerPredictionRequest(int farmId, String crop, String soilType) { this.farmId = farmId; this.crop = crop; this.soilType = soilType; }
}
