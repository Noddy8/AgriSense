package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.data.MockDataProvider;
import com.ninjaTurtles.agrisense.models.SensorData;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;

public class SensorDataActivity extends AppCompatActivity {

    private TextView tvMoisture, tvTemp;
    private ProgressBar pbMoisture;
    private MaterialButton btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        tvMoisture = findViewById(R.id.tvDetailMoistureValue);
        tvTemp = findViewById(R.id.tvDetailTempValue);
        pbMoisture = findViewById(R.id.pbMoisture);
        btnHistory = findViewById(R.id.btnViewSensorHistory);

        SensorData data = MockDataProvider.getMockSensorData();
        AnimationHelper.animateNumberValue(tvMoisture, 0, data.getSoilMoisture(), "%", 800);
        tvTemp.setText(data.getTemperature() + "°C");
        pbMoisture.setProgress(data.getSoilMoisture());

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(SensorDataActivity.this, btnHistory);
                startActivity(new Intent(SensorDataActivity.this, SensorHistoryActivity.class));
            }
        });
    }
}
