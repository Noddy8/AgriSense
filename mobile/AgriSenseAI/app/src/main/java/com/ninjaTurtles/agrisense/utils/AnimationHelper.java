package com.ninjaTurtles.agrisense.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.ninjaTurtles.agrisense.R;

public class AnimationHelper {

    /**
     * Applies a subtle press animation scale effect (100% -> 96% -> 100%) to any View.
     */
    public static void animateButtonPress(Context context, View view) {
        if (context == null || view == null) return;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.button_press);
        view.startAnimation(animation);
    }

    /**
     * Smoothly animates an integer numerical sensor value (e.g. 42% -> 68%).
     */
    public static void animateNumberValue(final TextView textView, int startVal, int endVal, final String suffix, int duration) {
        if (textView == null) return;
        ValueAnimator animator = ValueAnimator.ofInt(startVal, endVal);
        animator.setDuration(duration > 0 ? duration : 800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                textView.setText(value + (suffix != null ? suffix : ""));
            }
        });
        animator.start();
    }

    /**
     * Smoothly animates the height of a View representing a water tank level percentage.
     */
    public static void animateTankLevel(final View tankFillView, int startPercent, int endPercent, final int maxHeightPx) {
        if (tankFillView == null || maxHeightPx <= 0) return;
        ValueAnimator animator = ValueAnimator.ofInt(startPercent, endPercent);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int percent = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = tankFillView.getLayoutParams();
                params.height = (maxHeightPx * Math.min(Math.max(percent, 0), 100)) / 100;
                tankFillView.setLayoutParams(params);
            }
        });
        animator.start();
    }

    /**
     * Starts an infinite pulsing animation on a status indicator (e.g., Live dot).
     */
    public static void startPulse(Context context, View view) {
        if (context == null || view == null) return;
        Animation pulseAnim = AnimationUtils.loadAnimation(context, R.anim.pulse);
        view.startAnimation(pulseAnim);
    }

    /**
     * Stops any ongoing animation on a View.
     */
    public static void stopPulse(View view) {
        if (view != null) {
            view.clearAnimation();
        }
    }
}
