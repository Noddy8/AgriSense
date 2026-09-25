package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.FertilizerPlanActivity;
import com.ninjaTurtles.agrisense.activities.RecommendationsActivity;
import com.google.android.material.button.MaterialButton;

public class AlertsFragment extends Fragment {

    private TextView tvMarkAllRead;
    private MaterialButton btnActivatePump, btnViewFertilizerPlan, btnScheduleReminder;
    private MaterialButton btnReviewWeatherRadar, btnReviewCropProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alerts, container, false);

        tvMarkAllRead = v.findViewById(R.id.tvMarkAllRead);
        btnActivatePump = v.findViewById(R.id.btnActivatePump);
        btnViewFertilizerPlan = v.findViewById(R.id.btnViewFertilizerPlan);
        btnScheduleReminder = v.findViewById(R.id.btnScheduleReminder);
        btnReviewWeatherRadar = v.findViewById(R.id.btnReviewWeatherRadar);
        btnReviewCropProfile = v.findViewById(R.id.btnReviewCropProfile);

        if (tvMarkAllRead != null) {
            tvMarkAllRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "All alerts marked as read", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnActivatePump != null) {
            btnActivatePump.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Solar Pump Override Activated for Main Reservoir!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnViewFertilizerPlan != null) {
            btnViewFertilizerPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), FertilizerPlanActivity.class));
                }
            });
        }

        if (btnScheduleReminder != null) {
            btnScheduleReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Urea Application Reminder Scheduled for 48h", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnReviewWeatherRadar != null) {
            btnReviewWeatherRadar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Opening Weather Radar Station...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnReviewCropProfile != null) {
            btnReviewCropProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), RecommendationsActivity.class));
                }
            });
        }

        return v;
    }
}
