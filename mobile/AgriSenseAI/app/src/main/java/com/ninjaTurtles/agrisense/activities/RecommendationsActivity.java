package com.ninjaTurtles.agrisense.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.adapters.RecommendationAdapter;
import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.CropPredictionResponse;
import com.ninjaTurtles.agrisense.models.FertilizerOptions;
import com.ninjaTurtles.agrisense.models.FertilizerPredictionRequest;
import com.ninjaTurtles.agrisense.models.FertilizerPredictionResponse;
import com.ninjaTurtles.agrisense.models.Farm;
import com.ninjaTurtles.agrisense.models.Recommendation;
import com.ninjaTurtles.agrisense.models.SensorReading;
import com.ninjaTurtles.agrisense.network.AgriApiService;
import com.ninjaTurtles.agrisense.network.ApiClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationsActivity extends BaseActivity {
    private AgriApiService api;
    private RecommendationAdapter adapter;
    private Spinner cropSpinner, soilSpinner, scenarioSpinner;
    private TextView status, title;
    private TextView emptyState;
    private Button recommendButton, simulateButton;
    private int farmId = 0;
    private String type = "crop";
    private Farm activeFarm;
    private String latestSimulationSummary = "";
    private final String[] scenarioKeys = {"normal", "low-rainfall", "high-temperature", "low-nitrogen", "high-humidity"};
    private final String[] scenarioLabels = {"Normal conditions", "Low rainfall", "High temperature", "Low nitrogen", "High humidity"};

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        MaterialToolbar toolbar = findViewById(R.id.toolbarRecommendations);
        toolbar.setNavigationOnClickListener(v -> finish());
        api = ApiClient.getApiService();
        type = getIntent().getStringExtra("type");
        if (type == null) type = "crop";
        farmId = AgriRepository.getInstance().getActiveFarmId();
        activeFarm = null;
        title = findViewById(R.id.tvRecoTitle);
        status = findViewById(R.id.tvRecoStatus);
        emptyState = findViewById(R.id.tvRecoEmpty);
        cropSpinner = findViewById(R.id.spinnerRecoCrop);
        soilSpinner = findViewById(R.id.spinnerRecoSoil);
        scenarioSpinner = findViewById(R.id.spinnerScenario);
        recommendButton = findViewById(R.id.btnGetRecommendations);
        simulateButton = findViewById(R.id.btnApplyScenario);
        recommendButton.setEnabled(false);
        simulateButton.setEnabled(false);
        RecyclerView list = findViewById(R.id.rvFullRecommendations);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecommendationAdapter();
        list.setAdapter(adapter);

        ArrayAdapter<String> scenarios = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, scenarioLabels);
        scenarioSpinner.setAdapter(scenarios);
        boolean fertilizer = "fertilizer".equalsIgnoreCase(type);
        title.setText(fertilizer ? "Fertilizer recommendation" : "Crop recommendation");
        cropSpinner.setVisibility(fertilizer ? View.VISIBLE : View.GONE);
        soilSpinner.setVisibility(fertilizer ? View.VISIBLE : View.GONE);
        recommendButton.setText(fertilizer ? "Analyze fertilizer needs" : "Recommend crops from latest reading");
        recommendButton.setOnClickListener(v -> loadRecommendation());
        simulateButton.setOnClickListener(v -> applyScenario());
        loadFarmAndOptions(fertilizer);
    }

    private void loadFarmAndOptions(boolean fertilizer) {
        status.setText("Loading farm profile…");
        api.getFarms().enqueue(new Callback<List<Farm>>() {
            @Override public void onResponse(Call<List<Farm>> call, Response<List<Farm>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    activeFarm = response.body().get(0);
                    farmId = activeFarm.getFarmId();
                    simulateButton.setEnabled(true);
                    recommendButton.setEnabled(true);
                    status.setText("Farm: " + activeFarm.getName() + " · Soil profile: " + (activeFarm.getSoilType() == null ? "not set" : activeFarm.getSoilType()));
                    if (fertilizer) loadOptions(); else applyScenario();
                } else if (response.isSuccessful()) {
                    showFailure("The backend returned no farms for this signed-in account. Refresh the Farm screen or create a farm.");
                } else {
                    showFailure("Farm request failed (HTTP " + response.code() + "): " + errorText(response));
                }
            }
            @Override public void onFailure(Call<List<Farm>> call, Throwable error) { showFailure("Backend connection failed: " + error.getMessage()); }
        });
    }

    private void loadOptions() {
        status.setText("Loading fertilizer model choices…");
        api.getFertilizerOptions().enqueue(new Callback<FertilizerOptions>() {
            @Override public void onResponse(Call<FertilizerOptions> call, Response<FertilizerOptions> response) {
                FertilizerOptions options = response.body();
                if (!response.isSuccessful() || options == null || options.crops == null || options.soilTypes == null) {
                    showFailure("Fertilizer model is unavailable. Start the fertilizer AI service on port 8001.");
                    recommendButton.setEnabled(false);
                    return;
                }
                cropSpinner.setAdapter(new ArrayAdapter<>(RecommendationsActivity.this, android.R.layout.simple_spinner_dropdown_item, options.crops));
                soilSpinner.setAdapter(new ArrayAdapter<>(RecommendationsActivity.this, android.R.layout.simple_spinner_dropdown_item, options.soilTypes));
                selectSoilType(options.soilTypes, activeFarm == null ? null : activeFarm.getSoilType());
                status.setText("Choose the crop and the model soil category. Readings come from the selected farm.");
                recommendButton.setEnabled(true);
            }
            @Override public void onFailure(Call<FertilizerOptions> call, Throwable error) {
                showFailure("Fertilizer AI is not reachable on port 8001. Start its Python service.");
                recommendButton.setEnabled(false);
            }
        });
    }

    private void selectSoilType(List<String> supported, String farmSoil) {
        if (farmSoil == null) return;
        for (int i = 0; i < supported.size(); i++) {
            if (supported.get(i).equalsIgnoreCase(farmSoil.trim())) { soilSpinner.setSelection(i); return; }
        }
    }

    private void loadRecommendation() {
        recommendButton.setEnabled(false);
        adapter.setRecommendations(new ArrayList<Recommendation>());
        emptyState.setVisibility(View.VISIBLE);
        emptyState.setText("Getting an analysis from the latest saved sensor reading…");
        if ("fertilizer".equalsIgnoreCase(type)) {
            String crop = selected(cropSpinner), soilType = selected(soilSpinner);
            if (crop == null || soilType == null) { status.setText("The fertilizer model has no available crop or soil options."); recommendButton.setEnabled(true); return; }
            status.setText("Analyzing latest soil and climate readings…");
            api.getFertilizerRecommendation(new FertilizerPredictionRequest(farmId, crop, soilType)).enqueue(new Callback<FertilizerPredictionResponse>() {
                @Override public void onResponse(Call<FertilizerPredictionResponse> call, Response<FertilizerPredictionResponse> response) {
                    recommendButton.setEnabled(true);
                    if (!response.isSuccessful() || response.body() == null) { showFailure("Fertilizer recommendation failed: " + errorText(response)); return; }
                    FertilizerPredictionResponse result = response.body();
                    List<Recommendation> items = new ArrayList<>();
                    if (result.recommendations != null) for (FertilizerPredictionResponse.Prediction prediction : result.recommendations) {
                        String detail = "Model score " + percent(prediction.confidencePercentage) + ". This is a fertilizer-type prediction; the model does not provide a safe application rate.";
                        items.add(new Recommendation("fert-" + prediction.fertilizer, prediction.fertilizer, "Fertilizer · " + crop, detail, percent(prediction.confidencePercentage)));
                    }
                    adapter.setRecommendations(items);
                    emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                    if (items.isEmpty()) emptyState.setText("The fertilizer service returned no recommendations. Check its logs and try again.");
                    status.setText("Recommended: " + result.recommendedFertilizer + " · " + (result.input == null ? "latest farm reading" : result.input.soilType + " soil profile") + latestSimulationSummary);
                }
                @Override public void onFailure(Call<FertilizerPredictionResponse> call, Throwable error) { recommendButton.setEnabled(true); showFailure("Fertilizer AI request failed: " + error.getMessage()); }
            });
        } else {
            status.setText("Asking the crop model about the latest farm reading…");
            api.getCropRecommendation(farmId).enqueue(new Callback<CropPredictionResponse>() {
                @Override public void onResponse(Call<CropPredictionResponse> call, Response<CropPredictionResponse> response) {
                    recommendButton.setEnabled(true);
                    if (!response.isSuccessful() || response.body() == null) { showFailure("Crop recommendation failed: " + errorText(response)); return; }
                    CropPredictionResponse result = response.body();
                    List<Recommendation> items = new ArrayList<>();
                    if (result.recommendations != null) for (CropPredictionResponse.Crop crop : result.recommendations) {
                        items.add(new Recommendation("crop-" + crop.crop, crop.crop, "Crop match", "Model confidence " + percent(crop.confidencePercentage) + " using the latest simulated sensor profile.", percent(crop.confidencePercentage)));
                    }
                    adapter.setRecommendations(items);
                    emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                    if (items.isEmpty()) emptyState.setText("The crop model returned no recommendations. Check the model service and try again.");
                    status.setText("Top match: " + result.recommendedCrop + " · model inputs came from the latest saved sensor reading" + latestSimulationSummary);
                }
                @Override public void onFailure(Call<CropPredictionResponse> call, Throwable error) { recommendButton.setEnabled(true); showFailure("Crop AI request failed: " + error.getMessage()); }
            });
        }
    }

    private void applyScenario() {
        int index = scenarioSpinner.getSelectedItemPosition();
        simulateButton.setEnabled(false);
        recommendButton.setEnabled(false);
        adapter.setRecommendations(new ArrayList<Recommendation>());
        emptyState.setVisibility(View.VISIBLE);
        emptyState.setText("Generating and saving a randomized " + scenarioLabels[Math.max(0, index)].toLowerCase(Locale.getDefault()) + " sensor reading…");
        status.setText("Saving a simulated sensor reading…");
        api.applyScenario(farmId, scenarioKeys[Math.max(0, index)]).enqueue(new Callback<com.ninjaTurtles.agrisense.models.SensorReading>() {
            @Override public void onResponse(Call<com.ninjaTurtles.agrisense.models.SensorReading> call, Response<com.ninjaTurtles.agrisense.models.SensorReading> response) {
                simulateButton.setEnabled(true);
                if (!response.isSuccessful() || response.body() == null) { recommendButton.setEnabled(true); showFailure("Scenario could not be saved: " + errorText(response)); return; }
                SensorReading reading = response.body();
                latestSimulationSummary = String.format(Locale.getDefault(), " · reading #%d: N %.1f, P %.1f, K %.1f; %.1f°C, humidity %.1f%%, rain %.1f mm", reading.readingId, reading.nitrogen, reading.phosphorus, reading.potassium, reading.temperature, reading.humidity, reading.rainfall);
                status.setText("Reading saved. Getting model analysis…");
                loadRecommendation();
            }
            @Override public void onFailure(Call<com.ninjaTurtles.agrisense.models.SensorReading> call, Throwable error) { simulateButton.setEnabled(true); recommendButton.setEnabled(true); showFailure("Scenario request failed: " + error.getMessage()); }
        });
    }

    private void showFailure(String message) {
        status.setText(message);
        emptyState.setVisibility(View.VISIBLE);
        emptyState.setText(message);
    }

    private String selected(Spinner spinner) { Object value = spinner.getSelectedItem(); return value == null ? null : value.toString(); }
    private String percent(double value) { return String.format(Locale.getDefault(), "%.1f%%", value); }
    private String errorText(Response<?> response) {
        try {
            String body = response.errorBody() == null ? "" : response.errorBody().string();
            if (body.isEmpty()) return "No error details returned by the backend.";
            try {
                JSONObject json = new JSONObject(body);
                String message = json.optString("message", json.optString("Message", ""));
                if (!message.isEmpty()) return message;
            } catch (Exception ignored) { }
            return body;
        }
        catch (Exception ignored) { return "HTTP " + response.code(); }
    }
}
