package com.ninjaTurtles.agrisense.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ninjaTurtles.agrisense.data.MockDataProvider;
import com.ninjaTurtles.agrisense.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AiViewModel extends ViewModel {

    private MutableLiveData<List<ChatMessage>> chatMessagesLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isTypingLiveData = new MutableLiveData<>(false);
    private List<ChatMessage> messageList = new ArrayList<>();

    public AiViewModel() {
        messageList.addAll(MockDataProvider.getInitialChatHistory());
        chatMessagesLiveData.setValue(new ArrayList<>(messageList));
    }

    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessagesLiveData;
    }

    public LiveData<Boolean> getIsTyping() {
        return isTypingLiveData;
    }

    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        messageList.add(new ChatMessage(text.trim(), ChatMessage.TYPE_USER, time));
        chatMessagesLiveData.setValue(new ArrayList<>(messageList));

        // Trigger AI simulated response
        isTypingLiveData.setValue(true);
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isTypingLiveData.setValue(false);
                String aiReply = generateAiReply(text.trim());
                String replyTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                messageList.add(new ChatMessage(aiReply, ChatMessage.TYPE_AI, replyTime));
                chatMessagesLiveData.setValue(new ArrayList<>(messageList));
            }
        }, 1800);
    }

    private String generateAiReply(String userQuery) {
        String lower = userQuery.toLowerCase();
        if (lower.contains("soil") || lower.contains("moisture")) {
            return "Based on live telematics, current soil moisture is 68% in Zone 1 (Wheat). Root zone hydration is optimal. Next irrigation cycle is scheduled for tomorrow at 6:00 AM.";
        } else if (lower.contains("irrigation") || lower.contains("water") || lower.contains("pump")) {
            return "Intelligent drip irrigation is currently standby. Main water tank is at 74% capacity. Flow rate sensors are calibrated at 14 L/min.";
        } else if (lower.contains("crop") || lower.contains("harvest") || lower.contains("yield")) {
            return "Wheat crop NDVI index shows 0.82 vigor. Nitrogen levels are ideal. Harvest window estimate: 14 to 18 days away under current thermal time projections.";
        } else if (lower.contains("disease") || lower.contains("leaf") || lower.contains("blight")) {
            return "Please select 'Analyze Plant' from the top menu to run visual CNN diagnosis on leaf photo uploads.";
        } else {
            return "AgriSense AI processed your query: '" + userQuery + "'. Recommendations updated based on real-time microclimate sensors.";
        }
    }
}
