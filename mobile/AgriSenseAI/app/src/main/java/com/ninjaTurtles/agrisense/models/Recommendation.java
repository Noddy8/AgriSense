package com.ninjaTurtles.agrisense.models;

public class Recommendation {
    private String id;
    private String title;
    private String category; // e.g. "Irrigation", "Crop Health", "Fertilizer"
    private String description;
    private String urgency; // "High", "Medium", "Low"

    public Recommendation(String id, String title, String category, String description, String urgency) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.urgency = urgency;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getUrgency() { return urgency; }
}
