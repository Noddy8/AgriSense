package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.DashboardResponse;
import com.ninjaTurtles.agrisense.models.SensorReading;
import com.ninjaTurtles.agrisense.network.AgriApiService;
import com.ninjaTurtles.agrisense.network.ApiClient;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorDataActivity extends BaseActivity {
    private TextView tvMoisture, tvTemp, tvUpdated;
    private ProgressBar pbMoisture;
    private MaterialButton btnHistory;
    private int currentFarmId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);
        tvMoisture = findViewById(R.id.tvDetailMoistureValue);
        tvTemp = findViewById(R.id.tvDetailTempValue);
        tvUpdated = findViewById(R.id.tvSensorLastUpdated);
        pbMoisture = findViewById(R.id.pbMoisture);
        btnHistory = findViewById(R.id.btnViewSensorHistory);
        tvUpdated.setText("Loading farm readings…");
        int farmId = getIntent().getIntExtra("farm_id", AgriRepository.getInstance().getActiveFarmId());
        if (farmId < 1) {
            tvUpdated.setText("Loading your farm before sensor readings…");
            ApiClient.getApiService().getFarms().enqueue(new Callback<java.util.List<com.ninjaTurtles.agrisense.models.Farm>>() {
                @Override public void onResponse(Call<java.util.List<com.ninjaTurtles.agrisense.models.Farm>> call, Response<java.util.List<com.ninjaTurtles.agrisense.models.Farm>> response) {
                    if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) { tvUpdated.setText("Could not load your farm (HTTP " + response.code() + ")."); return; }
                    loadReading(response.body().get(0).getFarmId());
                }
                @Override public void onFailure(Call<java.util.List<com.ninjaTurtles.agrisense.models.Farm>> call, Throwable error) { tvUpdated.setText("Backend unavailable. Check sign-in and connection."); }
            });
        } else loadReading(farmId);
        btnHistory.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(SensorDataActivity.this, btnHistory);
            Intent intent = new Intent(SensorDataActivity.this, SensorHistoryActivity.class);
            intent.putExtra("farm_id", currentFarmId);
            startActivity(intent);
        });
    }

    private void loadReading(int farmId) {
        currentFarmId = farmId;
        AgriApiService api = ApiClient.getApiService();
        api.getDashboard(farmId).enqueue(new Callback<DashboardResponse>() {
            @Override public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().latestReading == null) {
                    tvUpdated.setText("No reading available. Start a simulation scenario first.");
                    tvMoisture.setText("—"); tvTemp.setText("—"); pbMoisture.setProgress(0); return;
                }
                SensorReading r = response.body().latestReading;
                if (r.soilMoisture == null) tvMoisture.setText("—");
                else AnimationHelper.animateNumberValue(tvMoisture, 0, (int)Math.round(r.soilMoisture), "%", 500);
                tvTemp.setText(String.format(java.util.Locale.getDefault(), "%.1f°C", r.temperature));
                pbMoisture.setProgress(r.soilMoisture == null ? 0 : (int)Math.round(r.soilMoisture));
                tvUpdated.setText(("simulation".equalsIgnoreCase(r.source) ? "SIMULATION MODE · " : "DEVICE · ") + r.recordedAt);
            }
            @Override public void onFailure(Call<DashboardResponse> call, Throwable error) {
                tvUpdated.setText("Backend unavailable: " + error.getMessage());
            }
        });
    }
}
