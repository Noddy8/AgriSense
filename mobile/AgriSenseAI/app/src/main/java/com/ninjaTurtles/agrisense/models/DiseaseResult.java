package com.ninjaTurtles.agrisense.models;

public class DiseaseResult {
    private String diseaseName;
    private int confidence; // percentage (e.g. 94)
    private String treatment;
    private String label;

    public DiseaseResult(String diseaseName, int confidence, String treatment, String label) {
        this.diseaseName = diseaseName;
        this.confidence = confidence;
        this.treatment = treatment;
        this.label = label;
    }

    public String getDiseaseName() { return diseaseName; }
    public int getConfidence() { return confidence; }
    public String getTreatment() { return treatment; }
    public String getLabel() { return label; }
}
