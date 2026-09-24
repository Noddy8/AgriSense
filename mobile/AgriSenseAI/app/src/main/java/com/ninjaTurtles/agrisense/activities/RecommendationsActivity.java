package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;

public class RecommendationsActivity extends AppCompatActivity {

    private ImageView btnBack, btnShare;
    private MaterialButton btnAdoptPlan, btnAskAiAdvisor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnAdoptPlan = findViewById(R.id.btnAdoptPlan);
        btnAskAiAdvisor = findViewById(R.id.btnAskAiAdvisor);

        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AgriSense AI Recommendation Report");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "AgriSense AI Recommendation Report: Basmati Rice (Pusa 1121) matched 95% for Field #2!");
                    startActivity(Intent.createChooser(shareIntent, "Share Recommendation via"));
                }
            });
        }

        if (btnAdoptPlan != null) {
            btnAdoptPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationHelper.animateButtonPress(RecommendationsActivity.this, btnAdoptPlan);
                    Toast.makeText(RecommendationsActivity.this, "Basmati Rice Plan Adopted for Field #2!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RecommendationsActivity.this, FertilizerPlanActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        if (btnAskAiAdvisor != null) {
            btnAskAiAdvisor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationHelper.animateButtonPress(RecommendationsActivity.this, btnAskAiAdvisor);
                    Toast.makeText(RecommendationsActivity.this, "Opening AI Farmer Assistant...", Toast.LENGTH_SHORT).show();
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

