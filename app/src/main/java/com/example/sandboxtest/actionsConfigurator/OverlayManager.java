package com.example.sandboxtest.actionsConfigurator;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.example.sandboxtest.R;
import com.example.sandboxtest.actionsConfigurator.OverlayView;

import java.util.HashMap;
import java.util.Map;

public class OverlayManager extends BroadcastReceiver {
    private Map<String, OverlayView> overlays = new HashMap<>();
    private WindowManager windowManager;

    public OverlayManager(Context context) {
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = intent.getParcelableExtra("_B_|_target_");
        String packageName = i.getPackage();
        switch (intent.getAction()) {
            case "com.example.sandboxtest.ACTION_CREATE_OVERLAY":
                if (!overlays.containsKey(packageName)) {
                    OverlayView overlay = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
                    overlay.init(windowManager, packageName);
                    overlays.put(packageName, overlay);
                }
                break;
            case "com.example.sandboxtest.ACTION_HIDE_OVERLAY":
                if (overlays.containsKey(packageName)) {
                    overlays.get(packageName).stop();
                }
                break;
            case "com.example.sandboxtest.ACTION_SHOW_OVERLAY":
                if (!overlays.containsKey(packageName)) {
                    OverlayView overlayView = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
                    overlayView.init(windowManager, packageName);
                    overlays.put(packageName, overlayView);
                }
                overlays.get(packageName).start();
                break;
            case "com.example.sandboxtest.ACTION_DESTROY_OVERLAY":
                if (overlays.containsKey(packageName)) {
                    if (overlays.containsKey(packageName)) {
                        overlays.get(packageName).destroy();
                        windowManager.removeView(overlays.get(packageName));
                        overlays.remove(packageName);
                    }
                }
                break;
        }
        Log.d("MyBroadcastReceiver", "onReceive: " + intent.getAction() + " da " + packageName);
    }

    /*public void showOverlay() {
        OverlayView overlay = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
        overlay.init(windowManager, "prova");
    }*/
}
