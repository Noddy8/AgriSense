package com.ninjaTurtles.agrisense.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ninjaTurtles.agrisense.models.AlertNotification;
import com.ninjaTurtles.agrisense.models.ChatMessage;
import com.ninjaTurtles.agrisense.models.DiseaseResult;
import com.ninjaTurtles.agrisense.models.Farm;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.models.Recommendation;
import com.ninjaTurtles.agrisense.models.SensorData;
import com.ninjaTurtles.agrisense.models.WeatherInfo;

import java.util.List;

public class AgriRepository {

    private static AgriRepository instance;

    private MutableLiveData<SensorData> sensorDataLiveData = new MutableLiveData<>();
    private MutableLiveData<IrrigationStatus> irrigationStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<List<AlertNotification>> alertsLiveData = new MutableLiveData<>();

    private AgriRepository() {
        sensorDataLiveData.setValue(MockDataProvider.getMockSensorData());
        irrigationStatusLiveData.setValue(new IrrigationStatus(IrrigationStatus.PumpState.OFF, 0, 0, "Zone 1 - Main Field"));
        alertsLiveData.setValue(MockDataProvider.getMockAlerts());
    }

    public static synchronized AgriRepository getInstance() {
        if (instance == null) {
            instance = new AgriRepository();
        }
        return instance;
    }

    public LiveData<SensorData> getSensorData() {
        return sensorDataLiveData;
    }

    public LiveData<IrrigationStatus> getIrrigationStatus() {
        return irrigationStatusLiveData;
    }

    public LiveData<List<AlertNotification>> getAlerts() {
        return alertsLiveData;
    }

    public LiveData<WeatherInfo> getWeather() {
        MutableLiveData<WeatherInfo> weatherData = new MutableLiveData<>();
        weatherData.setValue(MockDataProvider.getMockWeather());
        return weatherData;
    }

    public LiveData<List<Farm>> getFarms() {
        MutableLiveData<List<Farm>> farmsData = new MutableLiveData<>();
        farmsData.setValue(MockDataProvider.getMockFarms());
        return farmsData;
    }

    public LiveData<List<Recommendation>> getRecommendations() {
        MutableLiveData<List<Recommendation>> recsData = new MutableLiveData<>();
        recsData.setValue(MockDataProvider.getMockRecommendations());
        return recsData;
    }

    public void updatePumpState(IrrigationStatus.PumpState newState) {
        IrrigationStatus current = irrigationStatusLiveData.getValue();
        if (current != null) {
            current.setPumpState(newState);
            current.setFlowRateLpm(newState == IrrigationStatus.PumpState.ON ? 14 : 0);
            irrigationStatusLiveData.setValue(current);
        }
    }

    public DiseaseResult analyzeLeafImage() {
        return MockDataProvider.getMockDiseaseResult();
    }
}
