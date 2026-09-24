package com.ninjaTurtles.agrisense.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;

public class FertilizerPlanActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnScheduleFertilizer, btnAskAiAssistant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertilizer_plan);

        btnBack = findViewById(R.id.btnBack);
        btnScheduleFertilizer = findViewById(R.id.btnScheduleFertilizer);
        btnAskAiAssistant = findViewById(R.id.btnAskAiAssistant);

        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
        }

        if (btnScheduleFertilizer != null) {
            btnScheduleFertilizer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationHelper.animateButtonPress(FertilizerPlanActivity.this, btnScheduleFertilizer);
                    Toast.makeText(FertilizerPlanActivity.this, "Fertilizer Application Scheduled for Day 42 - 45", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnAskAiAssistant != null) {
            btnAskAiAssistant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationHelper.animateButtonPress(FertilizerPlanActivity.this, btnAskAiAssistant);
                    Toast.makeText(FertilizerPlanActivity.this, "Opening AI Farmer Assistant...", Toast.LENGTH_SHORT).show();
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
