package com.example.gamesconfigurator;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class OverlayManager {
    private OverlayView overlay;
    private TouchIndicatorView touchIndicatorView;

    public OverlayManager(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        overlay = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
        overlay.init(windowManager);

        touchIndicatorView = new TouchIndicatorView(context);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager.addView(touchIndicatorView, params);
    }

    public void changeGame(String packageName) {
        overlay.closeConfiguration();
        overlay.changeGame(packageName);
    }

    public void showOverlay() {
        overlay.start();
    }

    public void hideOverlay() {
        overlay.stop();
    }

    public TouchIndicatorView getTouchIndicatorView() {
        return touchIndicatorView;
    }
}
