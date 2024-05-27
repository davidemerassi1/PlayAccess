package com.example.accessibilityservice;

import static android.view.InputDevice.SOURCE_JOYSTICK;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyAccessibilityService extends AccessibilityService {
    private final String TAG = "MyAccessibilityService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "my_accessibility_channel";

    @Override
    public void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = getServiceInfo();
        if (info != null && Build.VERSION.SDK_INT >= 34) {
            // Imposta le sorgenti degli eventi di input generico
            info.setMotionEventSources(SOURCE_JOYSTICK);
            // Aggiorna le informazioni del servizio
            setServiceInfo(info);
            // Mostra la notifica
            showNotification();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getAction() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            Log.d(TAG, "Window state changed: " + event.getPackageName());
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(TAG, "Key down: " + event.getKeyCode());
            Intent intent = new Intent("com.example.accessibilityservice.ACTION_START");
            intent.putExtra("key_code", event.getKeyCode());
            intent.putExtra("source", event.getSource());
            sendBroadcast(intent);
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            Log.d(TAG, "Key up: " + event.getKeyCode());
            Intent intent = new Intent("com.example.accessibilityservice.ACTION_END");
            intent.putExtra("key_code", event.getKeyCode());
            intent.putExtra("source", event.getSource());
            sendBroadcast(intent);
        }
        return true;
    }

    @RequiresApi(api = 34)
    @Override
    public void onMotionEvent(@NonNull MotionEvent event) {
        Log.d(TAG, "MotionEvent: " + event + " x: " + event.getX() + " y: " + event.getY());
        super.onMotionEvent(event);
    }

    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Accessibility Service")
                .setContentText("Il servizio di accessibilità è attivo")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return;
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }
}
