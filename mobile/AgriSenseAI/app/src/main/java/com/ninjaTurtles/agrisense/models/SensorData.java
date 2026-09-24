package com.ninjaTurtles.agrisense.models;

public class SensorData {
    private int soilMoisture; // Percentage 0-100
    private double temperature; // Celsius
    private int humidity; // Percentage 0-100
    private int waterTankLevel; // Percentage 0-100
    private double waterFlowRate; // L/min
    private String lastUpdated;

    public SensorData() {}

    public SensorData(int soilMoisture, double temperature, int humidity, int waterTankLevel, double waterFlowRate, String lastUpdated) {
        this.soilMoisture = soilMoisture;
        this.temperature = temperature;
        this.humidity = humidity;
        this.waterTankLevel = waterTankLevel;
        this.waterFlowRate = waterFlowRate;
        this.lastUpdated = lastUpdated;
    }

    public int getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(int soilMoisture) { this.soilMoisture = soilMoisture; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public int getWaterTankLevel() { return waterTankLevel; }
    public void setWaterTankLevel(int waterTankLevel) { this.waterTankLevel = waterTankLevel; }

    public double getWaterFlowRate() { return waterFlowRate; }
    public void setWaterFlowRate(double waterFlowRate) { this.waterFlowRate = waterFlowRate; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
