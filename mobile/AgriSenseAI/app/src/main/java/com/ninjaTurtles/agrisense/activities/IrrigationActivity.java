package com.ninjaTurtles.agrisense.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;

public class IrrigationActivity extends AppCompatActivity {

    private TextView tvPumpStatusTitle, tvWaterFlowIndicator, tvTankPercent;
    private View viewWaterFillLevel, containerTankOutline;
    private MaterialButton btnStart, btnStop;

    private IrrigationStatus.PumpState currentPumpState = IrrigationStatus.PumpState.OFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation);

        tvPumpStatusTitle = findViewById(R.id.tvPumpStatusTitle);
        tvWaterFlowIndicator = findViewById(R.id.tvWaterFlowIndicator);
        tvTankPercent = findViewById(R.id.tvIrrigationTankPercent);
        viewWaterFillLevel = findViewById(R.id.viewWaterFillLevel);
        containerTankOutline = findViewById(R.id.containerTankOutline);
        btnStart = findViewById(R.id.btnStartIrrigation);
        btnStop = findViewById(R.id.btnStopIrrigation);

        // Animate tank fill height on open (0% -> 74%)
        containerTankOutline.post(new Runnable() {
            @Override
            public void run() {
                int maxHeight = containerTankOutline.getHeight();
                AnimationHelper.animateTankLevel(viewWaterFillLevel, 0, 74, maxHeight);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(IrrigationActivity.this, btnStart);
                if (currentPumpState == IrrigationStatus.PumpState.OFF) {
                    startPumpSequence();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(IrrigationActivity.this, btnStop);
                if (currentPumpState == IrrigationStatus.PumpState.ON || currentPumpState == IrrigationStatus.PumpState.STARTING) {
                    stopPumpSequence();
                }
            }
        });
    }

    private void startPumpSequence() {
        currentPumpState = IrrigationStatus.PumpState.STARTING;
        tvPumpStatusTitle.setText(R.string.pump_status_starting);
        tvPumpStatusTitle.setTextColor(getResources().getColor(R.color.accent, getTheme()));

        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        tvPumpStatusTitle.startAnimation(scaleAnim);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPumpState = IrrigationStatus.PumpState.ON;
                tvPumpStatusTitle.setText(R.string.pump_status_on);
                tvPumpStatusTitle.setTextColor(getResources().getColor(R.color.primary, getTheme()));

                // Show animated water flow indicator (💧 -> 💧 -> 💧)
                tvWaterFlowIndicator.setVisibility(View.VISIBLE);
                Animation bounce = AnimationUtils.loadAnimation(IrrigationActivity.this, R.anim.bounce);
                tvWaterFlowIndicator.startAnimation(bounce);
            }
        }, 1200);
    }

    private void stopPumpSequence() {
        currentPumpState = IrrigationStatus.PumpState.OFF;
        tvPumpStatusTitle.setText(R.string.pump_status_off);
        tvPumpStatusTitle.setTextColor(getResources().getColor(R.color.error, getTheme()));

        Animation fadeAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        tvPumpStatusTitle.startAnimation(fadeAnim);

        tvWaterFlowIndicator.clearAnimation();
        tvWaterFlowIndicator.setVisibility(View.GONE);
    }
}
