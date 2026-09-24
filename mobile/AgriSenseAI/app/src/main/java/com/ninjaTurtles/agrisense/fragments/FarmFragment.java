package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.IrrigationActivity;
import com.ninjaTurtles.agrisense.activities.RecommendationsActivity;
import com.ninjaTurtles.agrisense.activities.SensorDataActivity;
import com.ninjaTurtles.agrisense.activities.SensorHistoryActivity;
import com.google.android.material.card.MaterialCardView;

public class FarmFragment extends Fragment {

    private MaterialCardView cardModuleSoilSensors, cardModuleCropRec, cardModuleFertilizer, cardModuleIrrigation, cardModuleHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_farm, container, false);

        cardModuleSoilSensors = v.findViewById(R.id.cardModuleSoilSensors);
        cardModuleCropRec = v.findViewById(R.id.cardModuleCropRec);
        cardModuleFertilizer = v.findViewById(R.id.cardModuleFertilizer);
        cardModuleIrrigation = v.findViewById(R.id.cardModuleIrrigation);
        cardModuleHistory = v.findViewById(R.id.cardModuleHistory);

        setupClickListeners();

        return v;
    }

    private void setupClickListeners() {
        if (cardModuleSoilSensors != null) {
            cardModuleSoilSensors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SensorDataActivity.class));
                }
            });
        }

        if (cardModuleCropRec != null) {
            cardModuleCropRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), RecommendationsActivity.class));
                }
            });
        }

        if (cardModuleFertilizer != null) {
            cardModuleFertilizer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), RecommendationsActivity.class));
                }
            });
        }

        if (cardModuleIrrigation != null) {
            cardModuleIrrigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), IrrigationActivity.class));
                }
            });
        }

        if (cardModuleHistory != null) {
            cardModuleHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SensorHistoryActivity.class));
                }
            });
        }
    }
}
