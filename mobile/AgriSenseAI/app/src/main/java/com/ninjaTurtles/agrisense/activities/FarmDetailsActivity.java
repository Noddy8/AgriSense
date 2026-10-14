package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.google.android.material.button.MaterialButton;

public class FarmDetailsActivity extends BaseActivity {

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
            String crop = intent.getStringExtra("crop_type");
            tvCropType.setText("Crop Type: " + (crop == null ? "Choose for recommendation" : crop));
            tvFieldSize.setText(String.format(java.util.Locale.getDefault(), "Field Size: %.2f hectares", intent.getDoubleExtra("acres", 0.0)));
            tvSoilType.setText("Soil Type: " + intent.getStringExtra("soil_type"));
            int health = intent.getIntExtra("health_score", -1);
            tvHealth.setText("Health Score: " + (health < 0 ? "Not measured" : health + "%"));
        }

        btnViewSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmDetailsActivity.this, SensorDataActivity.class);
                intent.putExtra("farm_id", getIntent().getIntExtra("farm_id", 0));
                startActivity(intent);
            }
        });
    }
}
