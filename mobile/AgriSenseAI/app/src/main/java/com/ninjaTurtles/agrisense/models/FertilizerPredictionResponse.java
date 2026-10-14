package com.ninjaTurtles.agrisense.models;

import java.util.List;

public class FertilizerPredictionResponse {
    public boolean success;
    public String recommendedFertilizer;
    public String note;
    public Input input;
    public List<Prediction> recommendations;
    public static class Input {
        public String crop;
        @com.google.gson.annotations.SerializedName("soil_type") public String soilType;
        public double nitrogen, phosphorus, potassium, temperature, humidity, rainfall;
    }
    public static class Prediction {
        public String fertilizer;
        public double confidence;
        public double confidencePercentage;
    }
}
