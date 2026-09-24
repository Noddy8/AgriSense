package com.ninjaTurtles.agrisense.models;

public class WeatherInfo {
    private String condition; // e.g. "Sunny", "Light Rain", "Cloudy"
    private int temperature;
    private int rainProbability;
    private String location;

    public WeatherInfo(String condition, int temperature, int rainProbability, String location) {
        this.condition = condition;
        this.temperature = temperature;
        this.rainProbability = rainProbability;
        this.location = location;
    }

    public String getCondition() { return condition; }
    public int getTemperature() { return temperature; }
    public int getRainProbability() { return rainProbability; }
    public String getLocation() { return location; }
}
