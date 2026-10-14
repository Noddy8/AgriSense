package com.ninjaTurtles.agrisense.models;

import com.google.gson.annotations.SerializedName;

public class Farm {
    @SerializedName(value = "farmId", alternate = {"FarmId", "id", "Id"}) private int farmId;
    @SerializedName(value = "name", alternate = {"Name"})
    private String name;
    @SerializedName(value = "location", alternate = {"Location"})
    private String location;
    @SerializedName(value = "cropType", alternate = {"CropType"}) private String cropType;
    @SerializedName(value = "areaHectares", alternate = {"AreaHectares", "acres", "Acres"}) private double areaHectares;
    @SerializedName(value = "soilType", alternate = {"SoilType"})
    private String soilType;
    private int healthScore = -1;

    public Farm() { }
    public Farm(String id, String name, String cropType, double area, String soilType, int healthScore) {
        try { this.farmId = Integer.parseInt(id); } catch (Exception ignored) { this.farmId = 0; }
        this.name = name; this.cropType = cropType; this.areaHectares = area;
        this.soilType = soilType; this.healthScore = healthScore;
    }
    public int getFarmId() { return farmId; }
    public String getId() { return String.valueOf(farmId); }
    public String getName() { return name == null ? "My farm" : name; }
    public String getLocation() { return location; }
    public String getCropType() { return cropType; }
    public double getAcres() { return areaHectares; }
    public double getAreaHectares() { return areaHectares; }
    public String getSoilType() { return soilType; }
    public int getHealthScore() { return healthScore; }
}
