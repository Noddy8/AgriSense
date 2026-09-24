package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;

public class OnboardingActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "AgriSensePrefs";
    public static final String KEY_ONBOARDING_COMPLETED = "is_onboarding_completed";

    private int currentStep = 1;
    private final int TOTAL_STEPS = 4;

    // Header & Progress
    private ImageView btnHeaderBack;
    private TextView btnSkipOnboarding, tvStepIndicator, tvStepTitle;
    private ProgressBar onboardingProgressBar;

    // Containers for 4 steps
    private LinearLayout step1Container, step2Container, step3Container, step4Container;

    // Footer buttons
    private MaterialButton btnOnboardingBack, btnOnboardingContinue, btnCompleteOnboarding;

    // Step 1 Views
    private TextView tvAreaValue, tvAreaUnitLabel;
    private ImageView btnDecreaseArea, btnIncreaseArea;
    private TextView chipAcres, chipHectares, chipBigha, chipGuntha;
    private LinearLayout cardTerrainFlat, cardTerrainTerraced, cardTerrainPlateau;
    private LinearLayout btnRescanCoordinates;

    // Step 2 Views
    private LinearLayout cardSoilClayLoam, cardSoilSandyLoam, cardSoilBlackAlluvial, cardSoilRedLaterite;
    private TextView btnSoilLabYes, btnSoilLabNo, tvPhBadge, tvPhStatusDesc;
    private SeekBar seekBarPh;

    // Step 3 Views
    private LinearLayout cardCropRice, cardCropCotton;

    // Step 4 Views
    private LinearLayout cardIrrigationDrip, cardIrrigationSprinkler;

    // State Variables
    private double currentAreaAcres = 12.5;
    private String selectedUnit = "Acres";
    private String selectedTerrain = "Flat Lowland / Basin";
    private String selectedSoil = "Clay Loam";
    private boolean hasSoilLabReport = true;
    private double currentPh = 6.5;
    private String selectedIrrigation = "Sub-surface Drip Irrigation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initViews();
        setupStep1Listeners();
        setupStep2Listeners();
        setupStep3Listeners();
        setupStep4Listeners();
        setupNavigationListeners();

        updateStepUi();
    }

    private void initViews() {
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
        btnSkipOnboarding = findViewById(R.id.btnSkipOnboarding);
        tvStepIndicator = findViewById(R.id.tvStepIndicator);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        onboardingProgressBar = findViewById(R.id.onboardingProgressBar);

        step1Container = findViewById(R.id.step1Container);
        step2Container = findViewById(R.id.step2Container);
        step3Container = findViewById(R.id.step3Container);
        step4Container = findViewById(R.id.step4Container);

        btnOnboardingBack = findViewById(R.id.btnOnboardingBack);
        btnOnboardingContinue = findViewById(R.id.btnOnboardingContinue);
        btnCompleteOnboarding = findViewById(R.id.btnCompleteOnboarding);

        // Step 1
        tvAreaValue = findViewById(R.id.tvAreaValue);
        tvAreaUnitLabel = findViewById(R.id.tvAreaUnitLabel);
        btnDecreaseArea = findViewById(R.id.btnDecreaseArea);
        btnIncreaseArea = findViewById(R.id.btnIncreaseArea);
        chipAcres = findViewById(R.id.chipAcres);
        chipHectares = findViewById(R.id.chipHectares);
        chipBigha = findViewById(R.id.chipBigha);
        chipGuntha = findViewById(R.id.chipGuntha);
        cardTerrainFlat = findViewById(R.id.cardTerrainFlat);
        cardTerrainTerraced = findViewById(R.id.cardTerrainTerraced);
        cardTerrainPlateau = findViewById(R.id.cardTerrainPlateau);
        btnRescanCoordinates = findViewById(R.id.btnRescanCoordinates);

        // Step 2
        cardSoilClayLoam = findViewById(R.id.cardSoilClayLoam);
        cardSoilSandyLoam = findViewById(R.id.cardSoilSandyLoam);
        cardSoilBlackAlluvial = findViewById(R.id.cardSoilBlackAlluvial);
        cardSoilRedLaterite = findViewById(R.id.cardSoilRedLaterite);
        btnSoilLabYes = findViewById(R.id.btnSoilLabYes);
        btnSoilLabNo = findViewById(R.id.btnSoilLabNo);
        tvPhBadge = findViewById(R.id.tvPhBadge);
        tvPhStatusDesc = findViewById(R.id.tvPhStatusDesc);
        seekBarPh = findViewById(R.id.seekBarPh);

        // Step 3
        cardCropRice = findViewById(R.id.cardCropRice);
        cardCropCotton = findViewById(R.id.cardCropCotton);

        // Step 4
        cardIrrigationDrip = findViewById(R.id.cardIrrigationDrip);
        cardIrrigationSprinkler = findViewById(R.id.cardIrrigationSprinkler);
    }

    private void setupStep1Listeners() {
        btnIncreaseArea.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(this, btnIncreaseArea);
            currentAreaAcres += 0.5;
            tvAreaValue.setText(String.format("%.1f", currentAreaAcres));
        });

        btnDecreaseArea.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(this, btnDecreaseArea);
            if (currentAreaAcres > 0.5) {
                currentAreaAcres -= 0.5;
                tvAreaValue.setText(String.format("%.1f", currentAreaAcres));
            }
        });

        View.OnClickListener unitClickListener = v -> {
            chipAcres.setBackgroundResource(R.drawable.bg_input_box);
            chipAcres.setTextColor(getColor(R.color.text_secondary));
            chipHectares.setBackgroundResource(R.drawable.bg_input_box);
            chipHectares.setTextColor(getColor(R.color.text_secondary));
            chipBigha.setBackgroundResource(R.drawable.bg_input_box);
            chipBigha.setTextColor(getColor(R.color.text_secondary));
            chipGuntha.setBackgroundResource(R.drawable.bg_input_box);
            chipGuntha.setTextColor(getColor(R.color.text_secondary));

            TextView selectedChip = (TextView) v;
            selectedChip.setBackgroundResource(R.drawable.bg_pill_green_light);
            selectedChip.setTextColor(getColor(R.color.primary_dark));
            selectedUnit = selectedChip.getText().toString();
            tvAreaUnitLabel.setText(selectedUnit);
        };

        chipAcres.setOnClickListener(unitClickListener);
        chipHectares.setOnClickListener(unitClickListener);
        chipBigha.setOnClickListener(unitClickListener);
        chipGuntha.setOnClickListener(unitClickListener);

        if (btnRescanCoordinates != null) {
            btnRescanCoordinates.setOnClickListener(v -> {
                AnimationHelper.animateButtonPress(this, btnRescanCoordinates);
                Toast.makeText(this, "Refreshed live Sentinel-2 satellite feed & GPS coordinates.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupStep2Listeners() {
        View.OnClickListener soilClickListener = v -> {
            cardSoilClayLoam.setBackgroundResource(R.drawable.bg_card_selectable);
            cardSoilSandyLoam.setBackgroundResource(R.drawable.bg_card_selectable);
            cardSoilBlackAlluvial.setBackgroundResource(R.drawable.bg_card_selectable);
            cardSoilRedLaterite.setBackgroundResource(R.drawable.bg_card_selectable);

            v.setBackgroundResource(R.drawable.bg_card_selected);
        };

        if (cardSoilClayLoam != null) cardSoilClayLoam.setOnClickListener(soilClickListener);
        if (cardSoilSandyLoam != null) cardSoilSandyLoam.setOnClickListener(soilClickListener);
        if (cardSoilBlackAlluvial != null) cardSoilBlackAlluvial.setOnClickListener(soilClickListener);
        if (cardSoilRedLaterite != null) cardSoilRedLaterite.setOnClickListener(soilClickListener);

        if (btnSoilLabYes != null && btnSoilLabNo != null) {
            btnSoilLabYes.setOnClickListener(v -> {
                hasSoilLabReport = true;
                btnSoilLabYes.setBackgroundResource(R.drawable.bg_pill_green_light);
                btnSoilLabYes.setTextColor(getColor(R.color.primary_dark));
                btnSoilLabNo.setBackground(null);
                btnSoilLabNo.setTextColor(getColor(R.color.text_secondary));
            });

            btnSoilLabNo.setOnClickListener(v -> {
                hasSoilLabReport = false;
                btnSoilLabNo.setBackgroundResource(R.drawable.bg_pill_green_light);
                btnSoilLabNo.setTextColor(getColor(R.color.primary_dark));
                btnSoilLabYes.setBackground(null);
                btnSoilLabYes.setTextColor(getColor(R.color.text_secondary));
            });
        }

        if (seekBarPh != null) {
            seekBarPh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    currentPh = 4.5 + (progress * 0.1);
                    String phStr = String.format("%.1f pH", currentPh);
                    if (tvPhBadge != null) tvPhBadge.setText(phStr);

                    if (tvPhStatusDesc != null) {
                        if (currentPh < 6.0) {
                            tvPhStatusDesc.setText("Acidic Soil (Lime Treatment Recommended)");
                        } else if (currentPh <= 7.5) {
                            tvPhStatusDesc.setText("Slightly Acidic to Neutral (Ideal for Crops)");
                        } else {
                            tvPhStatusDesc.setText("Alkaline Soil (Gypsum Treatment Recommended)");
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private void setupStep3Listeners() {
        View.OnClickListener cropClickListener = v -> {
            if (cardCropRice != null) cardCropRice.setBackgroundResource(R.drawable.bg_card_selectable);
            if (cardCropCotton != null) cardCropCotton.setBackgroundResource(R.drawable.bg_card_selectable);

            v.setBackgroundResource(R.drawable.bg_card_selected);
        };

        if (cardCropRice != null) cardCropRice.setOnClickListener(cropClickListener);
        if (cardCropCotton != null) cardCropCotton.setOnClickListener(cropClickListener);
    }

    private void setupStep4Listeners() {
        View.OnClickListener irrigationClickListener = v -> {
            if (cardIrrigationDrip != null) cardIrrigationDrip.setBackgroundResource(R.drawable.bg_card_selectable);
            if (cardIrrigationSprinkler != null) cardIrrigationSprinkler.setBackgroundResource(R.drawable.bg_card_selectable);

            v.setBackgroundResource(R.drawable.bg_card_selected);
        };

        if (cardIrrigationDrip != null) cardIrrigationDrip.setOnClickListener(irrigationClickListener);
        if (cardIrrigationSprinkler != null) cardIrrigationSprinkler.setOnClickListener(irrigationClickListener);

        if (btnCompleteOnboarding != null) {
            btnCompleteOnboarding.setOnClickListener(v -> completeOnboardingAndProceed());
        }
    }

    private void setupNavigationListeners() {
        btnHeaderBack.setOnClickListener(v -> handleBackNavigation());
        btnOnboardingBack.setOnClickListener(v -> handleBackNavigation());

        btnOnboardingContinue.setOnClickListener(v -> {
            AnimationHelper.animateButtonPress(this, btnOnboardingContinue);
            if (currentStep < TOTAL_STEPS) {
                currentStep++;
                updateStepUi();
            } else {
                completeOnboardingAndProceed();
            }
        });

        btnSkipOnboarding.setOnClickListener(v -> completeOnboardingAndProceed());
    }

    private void handleBackNavigation() {
        if (currentStep > 1) {
            currentStep--;
            updateStepUi();
        } else {
            completeOnboardingAndProceed();
        }
    }

    private void updateStepUi() {
        tvStepIndicator.setText("Step " + currentStep + " of " + TOTAL_STEPS);
        int progress = (int) (((float) currentStep / TOTAL_STEPS) * 100);
        onboardingProgressBar.setProgress(progress);

        step1Container.setVisibility(currentStep == 1 ? View.VISIBLE : View.GONE);
        step2Container.setVisibility(currentStep == 2 ? View.VISIBLE : View.GONE);
        step3Container.setVisibility(currentStep == 3 ? View.VISIBLE : View.GONE);
        step4Container.setVisibility(currentStep == 4 ? View.VISIBLE : View.GONE);

        switch (currentStep) {
            case 1:
                tvStepTitle.setText("Step 1 of 4: Land Mapping");
                btnOnboardingBack.setVisibility(View.GONE);
                btnOnboardingContinue.setText("Continue →");
                break;
            case 2:
                tvStepTitle.setText("Step 2 of 4: Crop And Soil");
                btnOnboardingBack.setVisibility(View.VISIBLE);
                btnOnboardingContinue.setText("Continue →");
                break;
            case 3:
                tvStepTitle.setText("Step 3 of 4: AI Yield Profile");
                btnOnboardingBack.setVisibility(View.VISIBLE);
                btnOnboardingContinue.setText("Continue →");
                break;
            case 4:
                tvStepTitle.setText("Step 4 of 4: Advisory Preferences");
                btnOnboardingBack.setVisibility(View.VISIBLE);
                btnOnboardingContinue.setText("Complete & Proceed →");
                break;
        }

        // Scroll back to top on step transition
        View scrollView = findViewById(R.id.scrollViewContent);
        if (scrollView != null) {
            scrollView.scrollTo(0, 0);
        }
    }

    private void completeOnboardingAndProceed() {
        // Save onboarding completion state in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply();

        Toast.makeText(this, "Farm profile setup saved!", Toast.LENGTH_SHORT).show();

        // Navigate to LoginActivity (or MainActivity if authenticated)
        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        Intent intent;
        if (auth.getCurrentUser() != null) {
            intent = new Intent(OnboardingActivity.this, MainActivity.class);
        } else {
            intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
