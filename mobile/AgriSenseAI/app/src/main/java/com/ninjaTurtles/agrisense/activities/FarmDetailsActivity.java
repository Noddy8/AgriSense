package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.google.android.material.button.MaterialButton;

public class FarmDetailsActivity extends AppCompatActivity {

    private TextView tvFarmName, tvCropType, tvFieldSize, tvSoilType, tvHealth;
    private MaterialButton btnViewSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_details);

        tvFarmName = findViewById(R.id.tvDetailFarmName);
        tvCropType = findViewById(R.id.tvDetailCropType);
        tvFieldSize = findViewById(R.id.tvDetailFieldSize);
        tvSoilType = findViewById(R.id.tvDetailSoilType);
        tvHealth = findViewById(R.id.tvDetailHealth);
        btnViewSensors = findViewById(R.id.btnViewFarmSensors);

        Intent intent = getIntent();
        if (intent != null) {
            tvFarmName.setText(intent.getStringExtra("farm_name"));
            tvCropType.setText("Crop Type: " + intent.getStringExtra("crop_type"));
            tvFieldSize.setText("Field Size: " + intent.getDoubleExtra("acres", 10.0) + " Acres");
            tvSoilType.setText("Soil Type: " + intent.getStringExtra("soil_type"));
            tvHealth.setText("Health Score: " + intent.getIntExtra("health_score", 90) + "%");
        }

        btnViewSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FarmDetailsActivity.class.cast(v.getContext()), SensorDataActivity.class));
            }
        });
    }
}
