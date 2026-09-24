package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.IrrigationActivity;
import com.ninjaTurtles.agrisense.activities.RecommendationsActivity;
import com.ninjaTurtles.agrisense.activities.SensorDataActivity;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.models.SensorData;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.ninjaTurtles.agrisense.viewmodels.HomeViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    private MaterialCardView cardMoisture, cardTemp, cardHumidity, cardTank, cardIrrigationControl;
    private MaterialCardView cardRecCrop, cardRecIrrigation, cardRecFertilizer, cardViewAllSensors;
    private TextView tvValueMoisture, tvValueTemp, tvValueHumidity, tvValueTank, tvHomePumpStatus;
    private ProgressBar pbMoisture, pbTemp, pbHumidity, pbTank;
    private MaterialButton btnQuickControlIrrigation;
    private ImageView btnNotificationBell;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        cardMoisture = v.findViewById(R.id.cardMoisture);
        cardTemp = v.findViewById(R.id.cardTemp);
        cardHumidity = v.findViewById(R.id.cardHumidity);
        cardTank = v.findViewById(R.id.cardTank);
        cardIrrigationControl = v.findViewById(R.id.cardIrrigationControl);

        tvValueMoisture = v.findViewById(R.id.tvValueMoisture);
        tvValueTemp = v.findViewById(R.id.tvValueTemp);
        tvValueHumidity = v.findViewById(R.id.tvValueHumidity);
        tvValueTank = v.findViewById(R.id.tvValueTank);
        tvHomePumpStatus = v.findViewById(R.id.tvHomePumpStatus);

        pbMoisture = v.findViewById(R.id.pbMoisture);
        pbTemp = v.findViewById(R.id.pbTemp);
        pbHumidity = v.findViewById(R.id.pbHumidity);
        pbTank = v.findViewById(R.id.pbTank);

        btnQuickControlIrrigation = v.findViewById(R.id.btnQuickControlIrrigation);
        btnNotificationBell = v.findViewById(R.id.btnNotificationBell);

        cardRecCrop = v.findViewById(R.id.cardRecCrop);
        cardRecIrrigation = v.findViewById(R.id.cardRecIrrigation);
        cardRecFertilizer = v.findViewById(R.id.cardRecFertilizer);
        cardViewAllSensors = v.findViewById(R.id.cardViewAllSensors);

        setupClickListeners();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        runStaggeredDashboardAnimations();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getSensorData().observe(getViewLifecycleOwner(), new Observer<SensorData>() {
            @Override
            public void onChanged(SensorData data) {
                if (data != null) {
                    if (tvValueMoisture != null) tvValueMoisture.setText(String.valueOf(data.getSoilMoisture()));
                    if (tvValueHumidity != null) tvValueHumidity.setText(String.valueOf(data.getHumidity()));
                    if (tvValueTank != null) tvValueTank.setText(String.valueOf(data.getWaterTankLevel()));
                    if (tvValueTemp != null) tvValueTemp.setText(String.valueOf((int) data.getTemperature()));

                    if (pbMoisture != null) pbMoisture.setProgress(data.getSoilMoisture());
                    if (pbHumidity != null) pbHumidity.setProgress(data.getHumidity());
                    if (pbTank != null) pbTank.setProgress(data.getWaterTankLevel());
                    if (pbTemp != null) pbTemp.setProgress((int) Math.min(100, (data.getTemperature() / 50.0) * 100));
                }
            }
        });

        viewModel.getIrrigationStatus().observe(getViewLifecycleOwner(), new Observer<IrrigationStatus>() {
            @Override
            public void onChanged(IrrigationStatus status) {
                if (status != null && tvHomePumpStatus != null) {
                    if (status.getPumpState() == IrrigationStatus.PumpState.ON) {
                        tvHomePumpStatus.setText("Pump: ON");
                    } else if (status.getPumpState() == IrrigationStatus.PumpState.STARTING) {
                        tvHomePumpStatus.setText("Pump: STARTING");
                    } else {
                        tvHomePumpStatus.setText("Pump: OFF");
                    }
                }
            }
        });
    }

    private void runStaggeredDashboardAnimations() {
        if (getContext() == null) return;

        if (cardMoisture != null) {
            Animation anim1 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            anim1.setStartOffset(100);
            cardMoisture.startAnimation(anim1);
        }

        if (cardTemp != null) {
            Animation anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            anim2.setStartOffset(180);
            cardTemp.startAnimation(anim2);
        }

        if (cardIrrigationControl != null) {
            Animation scaleAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
            scaleAnim.setStartOffset(300);
            cardIrrigationControl.startAnimation(scaleAnim);
        }
    }

    private void setupClickListeners() {
        if (btnQuickControlIrrigation != null) {
            btnQuickControlIrrigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationHelper.animateButtonPress(getContext(), btnQuickControlIrrigation);
                    startActivity(new Intent(getActivity(), IrrigationActivity.class));
                }
            });
        }

        if (btnNotificationBell != null) {
            btnNotificationBell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "You have 2 new alerts & advisories", Toast.LENGTH_SHORT).show();
                }
            });
        }

        View.OnClickListener openSensorsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SensorDataActivity.class));
            }
        };

        if (cardMoisture != null) cardMoisture.setOnClickListener(openSensorsListener);
        if (cardTemp != null) cardTemp.setOnClickListener(openSensorsListener);
        if (cardHumidity != null) cardHumidity.setOnClickListener(openSensorsListener);
        if (cardTank != null) cardTank.setOnClickListener(openSensorsListener);
        if (cardViewAllSensors != null) cardViewAllSensors.setOnClickListener(openSensorsListener);

        View.OnClickListener openRecsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RecommendationsActivity.class));
            }
        };

        if (cardRecCrop != null) cardRecCrop.setOnClickListener(openRecsListener);
        if (cardRecIrrigation != null) cardRecIrrigation.setOnClickListener(openRecsListener);
        if (cardRecFertilizer != null) cardRecFertilizer.setOnClickListener(openRecsListener);
    }
}
