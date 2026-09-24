package com.ninjaTurtles.agrisense.models;

public class ChatMessage {
    public static final int TYPE_USER = 1;
    public static final int TYPE_AI = 2;

    private String text;
    private int type;
    private String timestamp;

    public ChatMessage(String text, int type, String timestamp) {
        this.text = text;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public int getType() { return type; }
    public String getTimestamp() { return timestamp; }
}
