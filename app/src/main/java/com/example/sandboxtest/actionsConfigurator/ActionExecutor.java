package com.example.sandboxtest.actionsConfigurator;

import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

public class ActionExecutor {
    private int statusBarHeight;

    public ActionExecutor(Context context) {
        statusBarHeight = getStatusBarHeight(context);
    }

    public void touch(Instrumentation instrumentation, int targetX, int targetY) {
        long now = SystemClock.uptimeMillis();
        MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, targetX, targetY + statusBarHeight, 0);

        Log.d("OverlayView", "Touching at " + targetX + ", " + targetY + statusBarHeight + "...");
        new Thread(() -> {
            try {
                instrumentation.sendPointerSync(touchEvent);
            } catch (SecurityException e) {
                //Toast.makeText(getContext(), "Errore durante la simulazione del tocco: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public void release(Instrumentation instrumentation, int targetX, int targetY) {
        long now = SystemClock.uptimeMillis();
        MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, targetX, targetY + statusBarHeight, 0);
        Log.d("OverlayView", "Releasing at " + targetX + ", " + targetY + statusBarHeight + "...");
        new Thread(() -> {
            try {
                instrumentation.sendPointerSync(touchEvent);
            } catch (SecurityException e) {
                //Toast.makeText(getContext(), "Errore durante la simulazione del tocco: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public void move(Instrumentation instrumentation, int toX, int toY) {
        long now = SystemClock.uptimeMillis();
        MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, toX, toY + statusBarHeight, 0);
        Log.d("OverlayView", "Moving to " + toX + ", " + toY + statusBarHeight + "...");
        new Thread(() -> {
            try {
                instrumentation.sendPointerSync(touchEvent);
            } catch (SecurityException e) {
                //Toast.makeText(getContext(), "Errore durante la simulazione del tocco: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        Log.d("OverlayView", "StatusBar height: " + statusBarHeight);
        return statusBarHeight;
    }
}
