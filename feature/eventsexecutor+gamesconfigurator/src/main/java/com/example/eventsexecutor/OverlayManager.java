package com.example.eventsexecutor;

import static android.content.Context.WINDOW_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class OverlayManager {
    private OverlayView overlay;

    public OverlayManager(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        overlay = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
        overlay.init(windowManager);
    }

    public void changeGame(String packageName) {
        overlay.changeGame(packageName);
    }

    public void showOverlay() {
        overlay.start();
    }

    public void hideOverlay() {
        overlay.stop();
    }
}
