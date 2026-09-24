package com.ninjaTurtles.agrisense.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.models.DiseaseResult;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.ninjaTurtles.agrisense.viewmodels.DiseaseViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class DiseaseDetectionActivity extends AppCompatActivity {

    private DiseaseViewModel viewModel;

    private ImageView imgLeafPhoto;
    private View viewScanningLine;
    private MaterialButton btnTakePhoto, btnChooseGallery, btnAnalyzePlant;
    private MaterialCardView cardResult, cardImageFrame;
    private TextView tvDiseaseName, tvConfidence, tvTreatment, tvResultLabel;

    private ObjectAnimator scanAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_detection);

        imgLeafPhoto = findViewById(R.id.imgLeafPhoto);
        viewScanningLine = findViewById(R.id.viewScanningLine);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChooseGallery = findViewById(R.id.btnChooseGallery);
        btnAnalyzePlant = findViewById(R.id.btnAnalyzePlant);
        cardResult = findViewById(R.id.cardDiseaseResult);
        cardImageFrame = findViewById(R.id.cardDiseaseImageFrame);

        tvDiseaseName = findViewById(R.id.tvDiseaseName);
        tvConfidence = findViewById(R.id.tvConfidence);
        tvTreatment = findViewById(R.id.tvTreatment);
        tvResultLabel = findViewById(R.id.tvResultLabel);

        viewModel = new ViewModelProvider(this).get(DiseaseViewModel.class);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(DiseaseDetectionActivity.this, btnTakePhoto);
                animateImageSelected();
                Toast.makeText(DiseaseDetectionActivity.this, "Photo Captured from Camera", Toast.LENGTH_SHORT).show();
            }
        });

        btnChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(DiseaseDetectionActivity.this, btnChooseGallery);
                animateImageSelected();
                Toast.makeText(DiseaseDetectionActivity.this, "Leaf Image Loaded from Gallery", Toast.LENGTH_SHORT).show();
            }
        });

        btnAnalyzePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(DiseaseDetectionActivity.this, btnAnalyzePlant);
                viewModel.analyzeImage();
            }
        });

        observeViewModel();
    }

    private void animateImageSelected() {
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        cardImageFrame.startAnimation(scaleUp);
    }

    private void observeViewModel() {
        viewModel.getIsAnalyzing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAnalyzing) {
                if (isAnalyzing) {
                    btnAnalyzePlant.setText(R.string.analyzing_plant);
                    btnAnalyzePlant.setEnabled(false);
                    cardResult.setVisibility(View.GONE);
                    startScanningLineAnimation();
                } else {
                    btnAnalyzePlant.setText(R.string.analyze_plant);
                    btnAnalyzePlant.setEnabled(true);
                    stopScanningLineAnimation();
                }
            }
        });

        viewModel.getDiseaseResult().observe(this, new Observer<DiseaseResult>() {
            @Override
            public void onChanged(DiseaseResult result) {
                if (result != null) {
                    tvDiseaseName.setText(result.getDiseaseName());
                    tvConfidence.setText("Confidence: " + result.getConfidence() + "%");
                    tvTreatment.setText(result.getTreatment());
                    tvResultLabel.setText(result.getLabel());

                    cardResult.setVisibility(View.VISIBLE);
                    Animation slideUp = AnimationUtils.loadAnimation(DiseaseDetectionActivity.this, R.anim.slide_up);
                    cardResult.startAnimation(slideUp);
                }
            }
        });
    }

    private void startScanningLineAnimation() {
        viewScanningLine.setVisibility(View.VISIBLE);
        float parentHeight = cardImageFrame.getHeight() > 0 ? cardImageFrame.getHeight() : 500f;

        scanAnimator = ObjectAnimator.ofFloat(viewScanningLine, "translationY", 0f, parentHeight - 10f);
        scanAnimator.setDuration(1000);
        scanAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scanAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scanAnimator.start();
    }

    private void stopScanningLineAnimation() {
        if (scanAnimator != null && scanAnimator.isRunning()) {
            scanAnimator.cancel();
        }
        viewScanningLine.setVisibility(View.GONE);
    }
}
