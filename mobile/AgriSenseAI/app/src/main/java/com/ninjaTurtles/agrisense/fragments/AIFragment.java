package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.DiseaseDetectionActivity;
import com.ninjaTurtles.agrisense.adapters.ChatAdapter;
import com.ninjaTurtles.agrisense.models.ChatMessage;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.ninjaTurtles.agrisense.viewmodels.AiViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AIFragment extends Fragment {

    private AiViewModel viewModel;
    private RecyclerView rvMessages;
    private ChatAdapter adapter;
    private EditText etInput;
    private ImageView btnSend;
    private TextView tvTypingIndicator;

    private MaterialButton btnQuickSoil, btnQuickIrrigation, btnQuickCrop, btnQuickDisease;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ai, container, false);

        rvMessages = v.findViewById(R.id.rvChatMessages);
        etInput = v.findViewById(R.id.etChatMessage);
        btnSend = v.findViewById(R.id.btnSendMessage);
        tvTypingIndicator = v.findViewById(R.id.tvTypingIndicator);

        btnQuickSoil = v.findViewById(R.id.btnQuickSoil);
        btnQuickIrrigation = v.findViewById(R.id.btnQuickIrrigation);
        btnQuickCrop = v.findViewById(R.id.btnQuickCrop);
        btnQuickDisease = v.findViewById(R.id.btnQuickDisease);

        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter();
        rvMessages.setAdapter(adapter);

        setupQuickButtons();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(getContext(), btnSend);
                String msg = etInput.getText().toString();
                if (!msg.trim().isEmpty()) {
                    viewModel.sendMessage(msg);
                    etInput.setText("");
                }
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AiViewModel.class);

        viewModel.getChatMessages().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> chatMessages) {
                adapter.setMessages(chatMessages);
                if (chatMessages.size() > 0) {
                    rvMessages.smoothScrollToPosition(chatMessages.size() - 1);
                }
            }
        });

        viewModel.getIsTyping().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTyping) {
                if (isTyping) {
                    tvTypingIndicator.setVisibility(View.VISIBLE);
                    AnimationHelper.startPulse(getContext(), tvTypingIndicator);
                } else {
                    tvTypingIndicator.clearAnimation();
                    tvTypingIndicator.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupQuickButtons() {
        btnQuickSoil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(getContext(), btnQuickSoil);
                viewModel.sendMessage("Check Soil Moisture Level");
            }
        });

        btnQuickIrrigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(getContext(), btnQuickIrrigation);
                viewModel.sendMessage("Irrigation Advice for Wheat");
            }
        });

        btnQuickCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(getContext(), btnQuickCrop);
                viewModel.sendMessage("Crop Health Status");
            }
        });

        btnQuickDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(getContext(), btnQuickDisease);
                startActivity(new Intent(getActivity(), DiseaseDetectionActivity.class));
            }
        });
    }
}
