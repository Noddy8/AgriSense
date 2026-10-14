package com.ninjaTurtles.agrisense.models;

import com.google.gson.annotations.SerializedName;

public class DashboardResponse {
    @SerializedName(value = "farm", alternate = {"Farm"})
    public Farm farm;
    @SerializedName(value = "latestReading", alternate = {"LatestReading"})
    public SensorReading latestReading;
    @SerializedName(value = "dataMode", alternate = {"DataMode"})
    public String dataMode;
}
