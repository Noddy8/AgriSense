package com.ninjaTurtles.agrisense.models;

import com.google.gson.annotations.SerializedName;

public class SensorReading {
    @SerializedName(value = "readingId", alternate = {"ReadingId"}) public long readingId;
    @SerializedName(value = "farmId", alternate = {"FarmId"}) public int farmId;
    @SerializedName(value = "source", alternate = {"Source"}) public String source;
    @SerializedName(value = "n", alternate = {"N"}) public double nitrogen;
    @SerializedName(value = "p", alternate = {"P"}) public double phosphorus;
    @SerializedName(value = "k", alternate = {"K"}) public double potassium;
    @SerializedName(value = "temperature", alternate = {"Temperature"})
    public double temperature;
    @SerializedName(value = "humidity", alternate = {"Humidity"})
    public double humidity;
    @SerializedName(value = "ph", alternate = {"Ph"}) public double soilPh;
    @SerializedName(value = "rainfall", alternate = {"Rainfall"})
    public double rainfall;
    @SerializedName(value = "soilMoisture", alternate = {"SoilMoisture"})
    public Double soilMoisture;
    @SerializedName(value = "recordedAt", alternate = {"RecordedAt"})
    public String recordedAt;
}
