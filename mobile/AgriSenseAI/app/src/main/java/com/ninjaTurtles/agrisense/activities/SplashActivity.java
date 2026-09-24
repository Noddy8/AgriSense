package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgLogo;
    private TextView tvTitle;
    private TextView tvTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgLogo = findViewById(R.id.imgSplashLogo);
        tvTitle = findViewById(R.id.tvSplashTitle);
        tvTagline = findViewById(R.id.tvSplashTagline);

        // Hide title & tagline initially for step sequence
        tvTitle.setAlpha(0f);
        tvTagline.setAlpha(0f);

        startSplashAnimationSequence();
    }

    private void startSplashAnimationSequence() {
        // Step 1 & 2: Logo fade-in & scale up (80% -> 100%)
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        imgLogo.startAnimation(scaleUp);

        // Step 3 & 4: Title fade in after 300ms
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeInTitle = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in);
                tvTitle.setAlpha(1f);
                tvTitle.startAnimation(fadeInTitle);
            }
        }, 300);

        // Step 5: Tagline slide upward & fade in after 500ms
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation slideUp = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.slide_up);
                tvTagline.setAlpha(1f);
                tvTagline.startAnimation(slideUp);
            }
        }, 500);

        // Step 6: After ~2.2 seconds -> transition to appropriate Activity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
                Intent intent;
                if (auth.getCurrentUser() != null) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }, 2200);
    }
}
