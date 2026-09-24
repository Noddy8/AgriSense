package com.ninjaTurtles.agrisense.models;

public class AlertNotification {
    private String id;
    private String title;
    private String message;
    private String time;
    private boolean isUnread;

    public AlertNotification(String id, String title, String message, String time, boolean isUnread) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isUnread = isUnread;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isUnread() { return isUnread; }
    public void setUnread(boolean unread) { isUnread = unread; }
}
