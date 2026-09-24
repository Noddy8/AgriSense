package com.ninjaTurtles.agrisense.models;

public class Farm {
    private String id;
    private String name;
    private String cropType;
    private double acres;
    private String soilType;
    private int healthScore;

    public Farm(String id, String name, String cropType, double acres, String soilType, int healthScore) {
        this.id = id;
        this.name = name;
        this.cropType = cropType;
        this.acres = acres;
        this.soilType = soilType;
        this.healthScore = healthScore;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCropType() { return cropType; }
    public double getAcres() { return acres; }
    public String getSoilType() { return soilType; }
    public int getHealthScore() { return healthScore; }
}
