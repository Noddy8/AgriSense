package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.DiseaseDetectionActivity;
import com.ninjaTurtles.agrisense.activities.FertilizerPlanActivity;
import com.google.android.material.button.MaterialButton;

public class AIFragment extends Fragment {

    private EditText etChatMessage;
    private ImageView btnSendMessage, btnCameraScan, btnVoiceMic;
    private View tabAgronomistAi, tabPlantDoctorScan;
    private TextView btnChipSoil, btnChipNitrogen;
    private MaterialButton btnScheduleUrea, btnFollowUpQuestion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ai, container, false);

        etChatMessage = v.findViewById(R.id.etChatMessage);
        btnSendMessage = v.findViewById(R.id.btnSendMessage);
        btnCameraScan = v.findViewById(R.id.btnCameraScan);
        btnVoiceMic = v.findViewById(R.id.btnVoiceMic);

        tabAgronomistAi = v.findViewById(R.id.tabAgronomistAi);
        tabPlantDoctorScan = v.findViewById(R.id.tabPlantDoctorScan);

        btnChipSoil = v.findViewById(R.id.btnChipSoil);
        btnChipNitrogen = v.findViewById(R.id.btnChipNitrogen);

        btnScheduleUrea = v.findViewById(R.id.btnScheduleUrea);
        btnFollowUpQuestion = v.findViewById(R.id.btnFollowUpQuestion);

        if (tabPlantDoctorScan != null) {
            tabPlantDoctorScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), DiseaseDetectionActivity.class));
                }
            });
        }

        if (btnCameraScan != null) {
            btnCameraScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), DiseaseDetectionActivity.class));
                }
            });
        }

        if (btnVoiceMic != null) {
            btnVoiceMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Listening in Punjabi / Hindi / English...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnChipSoil != null) {
            btnChipSoil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etChatMessage.setText("Explain my soil condition for Sector 4B");
                }
            });
        }

        if (btnChipNitrogen != null) {
            btnChipNitrogen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etChatMessage.setText("Why is nitrogen top-dressing needed this week?");
                }
            });
        }

        if (btnScheduleUrea != null) {
            btnScheduleUrea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), FertilizerPlanActivity.class));
                }
            });
        }

        if (btnFollowUpQuestion != null) {
            btnFollowUpQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etChatMessage.setText("What happens if I delay Urea application by 3 days?");
                }
            });
        }

        if (btnSendMessage != null) {
            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = etChatMessage.getText().toString().trim();
                    if (!text.isEmpty()) {
                        Toast.makeText(getContext(), "Asking AI Assistant: " + text, Toast.LENGTH_SHORT).show();
                        etChatMessage.setText("");
                    }
                }
            });
        }

        return v;
    }
}
