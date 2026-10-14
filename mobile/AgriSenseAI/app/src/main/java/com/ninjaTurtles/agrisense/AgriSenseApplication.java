package com.ninjaTurtles.agrisense;

import androidx.appcompat.app.AppCompatDelegate;

public class AgriSenseApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}
