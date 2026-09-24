package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;

public class SensorDataActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnViewSensorHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        btnBack = findViewById(R.id.btnBack);
        btnViewSensorHistory = findViewById(R.id.btnViewSensorHistory);

        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
        }

        if (btnViewSensorHistory != null) {
            btnViewSensorHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationHelper.animateButtonPress(SensorDataActivity.this, btnViewSensorHistory);
                    startActivity(new Intent(SensorDataActivity.this, SensorHistoryActivity.class));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
