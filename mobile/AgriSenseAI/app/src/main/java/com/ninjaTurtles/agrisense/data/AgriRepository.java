package com.ninjaTurtles.agrisense.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ninjaTurtles.agrisense.models.AlertNotification;
import com.ninjaTurtles.agrisense.models.DashboardResponse;
import com.ninjaTurtles.agrisense.models.DiseaseResult;
import com.ninjaTurtles.agrisense.models.Farm;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.models.Recommendation;
import com.ninjaTurtles.agrisense.models.SensorData;
import com.ninjaTurtles.agrisense.models.SensorReading;
import com.ninjaTurtles.agrisense.models.WeatherInfo;
import com.ninjaTurtles.agrisense.network.AgriApiService;
import com.ninjaTurtles.agrisense.network.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgriRepository {
    private static AgriRepository instance;
    private final AgriApiService api = ApiClient.getApiService();
    private final MutableLiveData<SensorData> sensorDataLiveData = new MutableLiveData<>();
    private final MutableLiveData<IrrigationStatus> irrigationStatusLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AlertNotification>> alertsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Farm>> farmsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Farm> activeFarmLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> connectionLiveData = new MutableLiveData<>("CONNECTING");
    private final MutableLiveData<List<Recommendation>> recommendationsLiveData = new MutableLiveData<>();
    private int activeFarmId = 0;
    private String activeFirebaseUid;

    private AgriRepository() {
        irrigationStatusLiveData.setValue(new IrrigationStatus(IrrigationStatus.PumpState.OFF, 0, 0, "Simulation only"));
        alertsLiveData.setValue(new ArrayList<AlertNotification>());
        recommendationsLiveData.setValue(new ArrayList<Recommendation>());
    }

    public static synchronized AgriRepository getInstance() {
        if (instance == null) instance = new AgriRepository();
        return instance;
    }

    public LiveData<SensorData> getSensorData() { return sensorDataLiveData; }
    public LiveData<IrrigationStatus> getIrrigationStatus() { return irrigationStatusLiveData; }
    public LiveData<List<AlertNotification>> getAlerts() { return alertsLiveData; }
    public LiveData<Farm> getActiveFarm() { return activeFarmLiveData; }
    public LiveData<String> getConnectionStatus() { return connectionLiveData; }
    public int getActiveFarmId() { return activeFarmId; }

    public void refreshFarms() {
        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) { clearUserSession(); connectionLiveData.setValue("SIGN IN REQUIRED"); return; }
        final String requestedUid = currentUser.getUid();
        if (!requestedUid.equals(activeFirebaseUid)) { clearUserData(); activeFirebaseUid = requestedUid; }
        api.getFarms().enqueue(new Callback<List<Farm>>() {
            @Override public void onResponse(Call<List<Farm>> call, Response<List<Farm>> response) {
                if (!isCurrentUser(requestedUid)) return;
                if (!response.isSuccessful() || response.body() == null) { connectionLiveData.setValue("OFFLINE"); return; }
                farmsLiveData.setValue(response.body());
                if (!response.body().isEmpty()) {
                    Farm farm = response.body().get(0);
                    activeFarmId = farm.getFarmId();
                    activeFarmLiveData.setValue(farm);
                    refreshDashboard();
                } else connectionLiveData.setValue("NO FARM");
            }
            @Override public void onFailure(Call<List<Farm>> call, Throwable error) { if (isCurrentUser(requestedUid)) connectionLiveData.setValue("OFFLINE"); }
        });
    }

    public LiveData<List<Farm>> getFarms() { refreshFarms(); return farmsLiveData; }

    public void refreshDashboard() {
        if (activeFarmId < 1) {
            connectionLiveData.setValue("LOADING FARM");
            return;
        }
        final int requestedFarmId = activeFarmId;
        final String requestedUid = activeFirebaseUid;
        if (requestedUid == null) { connectionLiveData.setValue("SIGN IN REQUIRED"); return; }
        api.getDashboard(requestedFarmId).enqueue(new Callback<DashboardResponse>() {
            @Override public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (!isCurrentUser(requestedUid) || activeFarmId != requestedFarmId) return;
                if (!response.isSuccessful() || response.body() == null) { connectionLiveData.setValue("OFFLINE"); return; }
                DashboardResponse dashboard = response.body();
                if (dashboard.farm != null) activeFarmLiveData.setValue(dashboard.farm);
                if (dashboard.latestReading != null) {
                    sensorDataLiveData.setValue(toSensorData(dashboard.latestReading));
                    connectionLiveData.setValue("SIMULATION".equalsIgnoreCase(dashboard.latestReading.source) ? "SIMULATION MODE" : "DEVICE CONNECTED");
                } else connectionLiveData.setValue("WAITING FOR DATA");
            }
            @Override public void onFailure(Call<DashboardResponse> call, Throwable error) { if (isCurrentUser(requestedUid)) connectionLiveData.setValue("OFFLINE"); }
        });
    }

    private SensorData toSensorData(SensorReading reading) {
        int moisture = reading.soilMoisture == null ? -1 : (int)Math.round(reading.soilMoisture);
        return new SensorData(moisture, reading.temperature, (int)Math.round(reading.humidity),
                -1, 0, reading.recordedAt, reading.nitrogen, reading.phosphorus, reading.potassium,
                reading.soilPh, reading.rainfall, reading.source);
    }

    public LiveData<WeatherInfo> getWeather() {
        MutableLiveData<WeatherInfo> weather = new MutableLiveData<>();
        weather.setValue(null); // Weather API is not part of the current backend.
        return weather;
    }

    public LiveData<List<Recommendation>> getRecommendations() { return recommendationsLiveData; }

    public void updatePumpState(IrrigationStatus.PumpState newState) {
        IrrigationStatus current = irrigationStatusLiveData.getValue();
        if (current != null) {
            current.setPumpState(newState);
            current.setFlowRateLpm(newState == IrrigationStatus.PumpState.ON ? 14 : 0);
            irrigationStatusLiveData.setValue(current);
        }
    }

    public void clearUserSession() {
        clearUserData();
        activeFirebaseUid = null;
    }

    private void clearUserData() {
        activeFarmId = 0;
        farmsLiveData.setValue(new ArrayList<Farm>());
        activeFarmLiveData.setValue(null);
        sensorDataLiveData.setValue(null);
        irrigationStatusLiveData.setValue(new IrrigationStatus(IrrigationStatus.PumpState.OFF, 0, 0, "Simulation only"));
        alertsLiveData.setValue(new ArrayList<AlertNotification>());
        recommendationsLiveData.setValue(new ArrayList<Recommendation>());
        connectionLiveData.setValue("SIGNED OUT");
    }

    private boolean isCurrentUser(String uid) {
        com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return uid != null && user != null && uid.equals(user.getUid());
    }

    // The disease endpoint is not yet implemented; leave the preview explicit until then.
    public DiseaseResult analyzeLeafImage() { return MockDataProvider.getMockDiseaseResult(); }
}
