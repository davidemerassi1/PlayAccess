package com.example.accessibilityservice;

import static android.view.InputDevice.SOURCE_JOYSTICK;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ExperimentalGetImage;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.example.actionsrecognizer.facialexpressionactionsrecognizer.FacialExpressionActionsRecognizer;

import java.util.List;

import it.unimi.di.ewlab.iss.common.model.ActionsChangedObserver;
import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;

public class MyAccessibilityService extends AccessibilityService implements ActionsChangedObserver {
    private String activePackage;
    private final String TAG = "MyAccessibilityService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "my_accessibility_channel";
    private BroadcastManager broadcastManager;
    private CameraLifecycle cameraLifecycle = new CameraLifecycle();
    private MainModel mainModel = MainModel.getInstance();

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    @Override
    public void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        broadcastManager = new BroadcastManager(this, cameraLifecycle);

        AccessibilityServiceInfo info = getServiceInfo();
        if (info != null && Build.VERSION.SDK_INT >= 34) {
            // Imposta le sorgenti degli eventi di input generico
            info.setMotionEventSources(SOURCE_JOYSTICK);
            // Aggiorna le informazioni del servizio
            setServiceInfo(info);
        }

        MainModel.observeActions(this);

        FacialExpressionActionsRecognizer.Companion.getInstance(MainModel.getInstance().getActions(), List.of(broadcastManager)).init(
                    this, cameraLifecycle
        );

        // Mostra la notifica
        showNotification();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (event.getPackageName() != null && !event.getPackageName().toString().equals(activePackage)) {
                    activePackage = event.getPackageName().toString();
                    Log.d(TAG, "Package changed: " + activePackage);
                    Intent intent = new Intent("com.example.accessibilityservice.PACKAGE_CHANGED");
                    intent.putExtra("packageName", activePackage);
                    sendBroadcast(intent);
                }
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_HOME || event.getKeyCode() == KeyEvent.KEYCODE_MENU || event.getKeyCode() == KeyEvent.KEYCODE_APP_SWITCH || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
            return super.onKeyEvent(event);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(TAG, "Key down: " + event.getKeyCode());
            ButtonAction buttonAction = mainModel.getButtonActionByKeyCode(event.getKeyCode());
            if (buttonAction != null) {
                broadcastManager.onActionStarts(buttonAction);
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            Log.d(TAG, "Key up: " + event.getKeyCode());
            ButtonAction buttonAction = mainModel.getButtonActionByKeyCode(event.getKeyCode());
            if (buttonAction != null) {
                broadcastManager.onActionEnds(buttonAction);
            }
            if (mainModel.getTempButtonAction().getValue() == null)
                MainModel.getInstance().setTempButtonAction(new ButtonAction(mainModel.getNextActionId(), KeyEvent.keyCodeToString(event.getKeyCode()), String.valueOf(event.getSource()), String.valueOf(event.getKeyCode())));
        }
        return true;
    }

    @RequiresApi(api = 34)
    @Override
    public void onMotionEvent(@NonNull MotionEvent event) {
        Log.d(TAG, "MotionEvent: " + event + " x: " + event.getX() + " y: " + event.getY());

        ButtonAction prevAction = mainModel.getTempButtonAction().getValue();
        if (prevAction == null) {
            ButtonAction action = new ButtonAction(mainModel.getNextActionId(), KeyEvent.keyCodeToString(19), String.valueOf(event.getSource()), String.valueOf(19));
            action.setIs2d(true);
            mainModel.setTempButtonAction(action);
        }
        if (mainModel.getButtonActionByKeyCode(19) != null)
            broadcastManager.on2dMovement(mainModel.getButtonActionByKeyCode(19), event.getX(), event.getY());

        super.onMotionEvent(event);
    }

    private void showNotification() {
        // Creazione del canale per le notifiche (necessario per Android 8.0 e versioni successive)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Accessibility Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("PlayAccess Accessibility Service")
                .setContentText("Il servizio di accessibilità è attivo")
                .setSmallIcon(R.drawable.playaccess_logo_notification)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return;
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void onActionsChanged(Action removedAction) {
        FacialExpressionActionsRecognizer.Companion.getInstance().updateActions(mainModel.getActions());
        if (removedAction != null)
            broadcastManager.removeAction(removedAction);
    }
}
