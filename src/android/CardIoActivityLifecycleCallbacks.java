package com.os.mobile.cardioplugin;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.card.payment.CardIOActivity;
import io.card.payment.DataEntryActivity;

/**
 * Listener for Card IO Activities lifecycle.
 * The main goal being to fix the insets on Android 15 (since edge-to-edge is enforced with targetSdk=35).
 * Checks when an activity is in foreground (onResume), and applying the proper insets to the root layout.
 * This way, there is no need to provide a custom implementation/fork of Card IO.
 */
public class CardIoActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}


    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (!(activity instanceof CardIOActivity || activity instanceof DataEntryActivity)) {
            return;
        }
        View activityLayoutRootView =
                ((ViewGroup) activity.getWindow().getDecorView().getRootView()).getChildAt(0);
        if (activityLayoutRootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    activityLayoutRootView,
                    (v, insets) -> {
                        Insets systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        v.setPadding(
                                systemBarInsets.left,
                                systemBarInsets.top,
                                systemBarInsets.right,
                                systemBarInsets.bottom
                        );
                        return insets;
                    }
            );
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}
}
