package com.ninjaTurtles.agrisense.models;

public class IrrigationStatus {
    public enum PumpState { OFF, STARTING, ON }

    private PumpState pumpState;
    private int flowRateLpm;
    private int sessionMinutes;
    private String zoneName;

    public IrrigationStatus(PumpState pumpState, int flowRateLpm, int sessionMinutes, String zoneName) {
        this.pumpState = pumpState;
        this.flowRateLpm = flowRateLpm;
        this.sessionMinutes = sessionMinutes;
        this.zoneName = zoneName;
    }

    public PumpState getPumpState() { return pumpState; }
    public void setPumpState(PumpState pumpState) { this.pumpState = pumpState; }

    public int getFlowRateLpm() { return flowRateLpm; }
    public void setFlowRateLpm(int flowRateLpm) { this.flowRateLpm = flowRateLpm; }

    public int getSessionMinutes() { return sessionMinutes; }
    public void setSessionMinutes(int sessionMinutes) { this.sessionMinutes = sessionMinutes; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
}
