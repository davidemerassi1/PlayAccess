package it.unimi.di.ewlab.iss.accessibilityservice;

import static android.view.InputDevice.SOURCE_JOYSTICK;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
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

import it.unimi.di.ewlab.iss.actionsrecognizer.FacialExpressionActionsRecognizer;
import it.unimi.di.ewlab.iss.gamesconfigurator.OverlayManager;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
    private OverlayManager overlayManager;
    private EventExecutor executor;
    @ExperimentalGetImage
    private FacialExpressionActionsRecognizer facialExpressionActionsRecognizer;
    private int orientation = Configuration.ORIENTATION_UNDEFINED;
    private Handler handler;
    private String launcher;

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

        overlayManager = new OverlayManager(this);

        executor = new EventExecutor(this, overlayManager.getTouchIndicatorView());

        if (mainModel.getNeutralFacialExpressionAction() != null) {
            facialExpressionActionsRecognizer = FacialExpressionActionsRecognizer.Companion.getInstance(MainModel.getInstance().getActions(), List.of(executor));
            facialExpressionActionsRecognizer.init(this, cameraLifecycle);
        }

        // Mostra la notifica
        showNotification();

        launcher = getDefaultLauncherPackageName();

        handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (motionX != null && motionY != null) {
                    broadcastManager.on2dMovement(mainModel.getButtonActionByKeyCode(19), motionX, motionY);
                    executor.on2dMovement(mainModel.getButtonActionByKeyCode(19), motionX, motionY);
                    motionX = null;
                    motionY = null;
                }
                handler.postDelayed(this, 100);
            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Configuration newConfig = getResources().getConfiguration();
                if (orientation != newConfig.orientation) {
                    orientation = newConfig.orientation;
                    changeOrientation();
                }
                if (event.getPackageName() != null) {
                    if (event.getPackageName().toString().equals(launcher)) {
                        overlayManager.hideOverlay();
                        executor.pause();
                        return;
                    }
                    activePackage = getForegroundApp();
                    if (activePackage.equals(getPackageName())) {
                        overlayManager.hideOverlay();
                        executor.pause();
                        return;
                    }
                    Log.d(TAG, "Package changed: " + event.getPackageName() + " " + activePackage);
                    Intent intent = new Intent("it.unimi.di.ewlab.iss.accessibilityservice.PACKAGE_CHANGED");
                    intent.putExtra("packageName", activePackage);
                    sendBroadcast(intent);
                    overlayManager.changeGame(activePackage);
                    overlayManager.showOverlay();
                    executor.changeGame(activePackage);
                    executor.resume();
                }
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                switch (event.getText().toString()) {
                    case "[RELOAD_ASSOCIATIONS]" -> executor.changeGame(activePackage);
                    case "[CONFIGURATION_OPENED]" -> executor.pause();
                }
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void changeOrientation() {
        Log.d(TAG, "Orientation changed: " + orientation);
        if (facialExpressionActionsRecognizer == null)
            return;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            facialExpressionActionsRecognizer.setPrecision(mainModel.getPrecision());
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            facialExpressionActionsRecognizer.setPrecision(mainModel.getPrecision() * 2f);
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
                executor.onActionStarts(buttonAction);
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            Log.d(TAG, "Key up: " + event.getKeyCode());
            ButtonAction buttonAction = mainModel.getButtonActionByKeyCode(event.getKeyCode());
            if (buttonAction != null) {
                broadcastManager.onActionEnds(buttonAction);
                executor.onActionEnds(buttonAction);
            }
            if (mainModel.getTempButtonAction().getValue() == null)
                MainModel.getInstance().setTempButtonAction(new ButtonAction(mainModel.getNextActionId(), KeyEvent.keyCodeToString(event.getKeyCode()), String.valueOf(event.getSource()), String.valueOf(event.getKeyCode())));
        }
        return true;
    }
    private Float motionX;
    private Float motionY;
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
        if (mainModel.getButtonActionByKeyCode(19) != null) {
            motionX = event.getX();
            motionY = event.getY();
        }

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
                .setOngoing(true)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return;
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void onActionsChanged(Action removedAction) {
        if (facialExpressionActionsRecognizer != null) {
            facialExpressionActionsRecognizer.updateActions(mainModel.getActions());
            if (removedAction != null)
                broadcastManager.removeAction(removedAction);
        } else {
            if (mainModel.getNeutralFacialExpressionAction() != null) {
                facialExpressionActionsRecognizer = FacialExpressionActionsRecognizer.Companion.getInstance(mainModel.getActions(), List.of(executor));
                facialExpressionActionsRecognizer.init(this, cameraLifecycle);
                onActionsChanged(removedAction);
            }
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void onPrecisionChanged(float precision) {
        facialExpressionActionsRecognizer.setPrecision(precision);
    }

    public void setCameraNeeded(boolean cameraNeeded) {
        if (cameraNeeded)
            cameraLifecycle.resume();
        else
            cameraLifecycle.pause();
    }

    public String getForegroundApp() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }

        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000 * 60;

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (usageStatsList == null || usageStatsList.isEmpty()) {
            Log.d(TAG, "No usage stats available");
            return null;
        }

        SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
        for (UsageStats usageStats : usageStatsList) {
            sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
        }

        if (!sortedMap.isEmpty()) {
            UsageStats recentUsageStats = sortedMap.get(sortedMap.lastKey());
            return recentUsageStats.getPackageName();
        }

        return null;
    }

    public String getDefaultLauncherPackageName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            return resolveInfo.activityInfo.packageName;
        }

        return null;
    }
}
