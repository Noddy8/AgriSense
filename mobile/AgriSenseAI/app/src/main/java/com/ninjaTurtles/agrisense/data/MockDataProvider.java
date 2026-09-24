package com.ninjaTurtles.agrisense.data;

import com.ninjaTurtles.agrisense.models.AlertNotification;
import com.ninjaTurtles.agrisense.models.ChatMessage;
import com.ninjaTurtles.agrisense.models.DiseaseResult;
import com.ninjaTurtles.agrisense.models.Farm;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.models.Recommendation;
import com.ninjaTurtles.agrisense.models.SensorData;
import com.ninjaTurtles.agrisense.models.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

public class MockDataProvider {

    public static SensorData getMockSensorData() {
        return new SensorData(68, 28.5, 62, 74, 4.2, "Just now");
    }

    public static WeatherInfo getMockWeather() {
        return new WeatherInfo("Partly Cloudy", 29, 15, "Punjab Sector 4");
    }

    public static List<Farm> getMockFarms() {
        List<Farm> farms = new ArrayList<>();
        farms.add(new Farm("1", "Green Valley Field A", "Wheat", 12.5, "Loamy Soil", 92));
        farms.add(new Farm("2", "Sunrise Orchard B", "Apples", 8.0, "Clay Loam", 85));
        farms.add(new Farm("3", "Highland Cotton Zone", "Cotton", 25.0, "Sandy Soil", 78));
        return farms;
    }

    public static List<Recommendation> getMockRecommendations() {
        List<Recommendation> list = new ArrayList<>();
        list.add(new Recommendation("r1", "Scheduled Irrigation Recommended", "Irrigation", "Soil moisture in Zone 2 drop predicted by 4 PM. Schedule 35-minute drip cycle.", "High"));
        list.add(new Recommendation("r2", "Nitrogen Booster Application", "Fertilizer", "Top-dressing with N-P-K (20-10-10) suggested during current vegetative growth stage.", "Medium"));
        list.add(new Recommendation("r3", "Optimal Harvesting Window", "Crop Health", "Wheat crop maturity index reached 94%. Favorable clear weather for next 3 days.", "Low"));
        return list;
    }

    public static List<AlertNotification> getMockAlerts() {
        List<AlertNotification> alerts = new ArrayList<>();
        alerts.add(new AlertNotification("a1", "Water Tank Low Warning", "Water level dropped to 18%. Automated refill queued.", "10 mins ago", true));
        alerts.add(new AlertNotification("a2", "High Soil Temperature", "Zone 3 sensor registered 34°C. Shading or light misting recommended.", "1 hour ago", true));
        alerts.add(new AlertNotification("a3", "Optimal Moisture Achieved", "Zone 1 drip cycle completed automatically. 450L saved.", "3 hours ago", false));
        alerts.add(new AlertNotification("a4", "Weather Alert: Heavy Rain Expected", "Rain forecast for tomorrow morning. Auto-irrigation paused.", "Yesterday", false));
        return alerts;
    }

    public static List<ChatMessage> getInitialChatHistory() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("Hello! I am AgriSense AI, your intelligent farming assistant. How can I help optimize your crop today?", ChatMessage.TYPE_AI, "10:00 AM"));
        return messages;
    }

    public static DiseaseResult getMockDiseaseResult() {
        return new DiseaseResult(
                "Tomato Leaf Blight",
                94,
                "Apply Copper-based fungicide spray every 7-10 days. Ensure optimal row spacing for air circulation and avoid overhead foliage watering.",
                "AI-Assisted Analysis"
        );
    }
}
