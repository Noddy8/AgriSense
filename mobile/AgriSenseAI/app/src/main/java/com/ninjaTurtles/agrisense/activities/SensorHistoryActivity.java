package com.ninjaTurtles.agrisense.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.SensorReading;
import com.ninjaTurtles.agrisense.network.AgriApiService;
import com.ninjaTurtles.agrisense.network.ApiClient;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorHistoryActivity extends BaseActivity {
    private LinearLayout rows;
    private TextView status;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_history);
        rows = findViewById(R.id.containerHistoryReadings);
        status = findViewById(R.id.tvHistoryStatus);
        int farmId = getIntent().getIntExtra("farm_id", AgriRepository.getInstance().getActiveFarmId());
        if (farmId < 1) { status.setText("Open sensor history from one of your farms."); return; }
        ApiClient.getApiService().getSensorHistory(farmId, 50).enqueue(new Callback<List<SensorReading>>() {
            @Override public void onResponse(Call<List<SensorReading>> call, Response<List<SensorReading>> response) {
                if (!response.isSuccessful() || response.body() == null) { status.setText("Could not load readings (HTTP " + response.code() + ")."); return; }
                if (response.body().isEmpty()) { status.setText("No readings saved for this farm yet."); return; }
                status.setText("Latest " + response.body().size() + " saved readings · newest first");
                for (SensorReading reading : response.body()) addReading(reading);
            }
            @Override public void onFailure(Call<List<SensorReading>> call, Throwable error) { status.setText("Backend unavailable: " + error.getMessage()); }
        });
    }

    private void addReading(SensorReading reading) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(10); card.setLayoutParams(cardParams); card.setRadius(dp(14)); card.setCardElevation(dp(1)); card.setUseCompatPadding(true);
        LinearLayout content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(16), dp(14), dp(16), dp(14));
        TextView date = new TextView(this); date.setText(("simulation".equalsIgnoreCase(reading.source) ? "Simulation · " : "Device · ") + (reading.recordedAt == null ? "" : reading.recordedAt)); date.setTextColor(getColor(R.color.primary_dark)); date.setTextSize(13);
        TextView values = new TextView(this); values.setText(String.format(Locale.getDefault(), "N %.0f  ·  P %.0f  ·  K %.0f\n%.1f°C  ·  Humidity %.0f%%  ·  Rain %.1f mm%s", reading.nitrogen, reading.phosphorus, reading.potassium, reading.temperature, reading.humidity, reading.rainfall, reading.soilMoisture == null ? "" : "  ·  Moisture " + String.format(Locale.getDefault(), "%.0f%%", reading.soilMoisture))); values.setTextColor(getColor(R.color.text_secondary)); values.setTextSize(13); values.setPadding(0, dp(8), 0, 0);
        content.addView(date); content.addView(values); card.addView(content); rows.addView(card);
    }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }
}
