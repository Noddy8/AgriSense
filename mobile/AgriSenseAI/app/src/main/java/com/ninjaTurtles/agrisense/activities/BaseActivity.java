package com.ninjaTurtles.agrisense.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/** Shared system-bar handling for every screen, including gesture-navigation devices. */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        boolean dark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        controller.setAppearanceLightStatusBars(!dark);
        controller.setAppearanceLightNavigationBars(!dark);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        View content = findViewById(android.R.id.content);
        if (content == null) return;
        content.setBackgroundColor(resolveBackgroundColor());
        View insetTarget = content instanceof ViewGroup && ((ViewGroup) content).getChildCount() > 0
                ? ((ViewGroup) content).getChildAt(0) : content;
        final int left = insetTarget.getPaddingLeft();
        final int top = insetTarget.getPaddingTop();
        final int right = insetTarget.getPaddingRight();
        final int bottom = insetTarget.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(insetTarget, (view, insets) -> {
            Insets safe = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(left + safe.left, top + safe.top, right + safe.right,
                    bottom + Math.max(safe.bottom, ime.bottom));
            return insets;
        });
        ViewCompat.requestApplyInsets(insetTarget);
    }

    private int resolveBackgroundColor() {
        android.util.TypedValue value = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, value, true);
        return value.data;
    }
}
