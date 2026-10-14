package com.ninjaTurtles.agrisense.models;

import java.util.List;

public class CropPredictionResponse {
    public boolean success;
    public String recommendedCrop;
    public List<Crop> recommendations;
    public static class Crop { public String crop; public double confidence; public double confidencePercentage; }
}
