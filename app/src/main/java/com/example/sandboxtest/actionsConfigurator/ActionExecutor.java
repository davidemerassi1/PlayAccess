package com.example.sandboxtest.actionsConfigurator;

import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.example.sandboxtest.database.Association;

public class ActionExecutor {
    private int statusBarHeight;
    private int screenWidth;
    private int screenHeight;
    private Instrumentation instrumentation;

    public ActionExecutor(Context context) {
        statusBarHeight = getStatusBarHeight(context);
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        instrumentation = new Instrumentation();
    }

    public void touch(int targetX, int targetY) {
        long now = SystemClock.uptimeMillis();
        //targetX = checkBoundX(targetX);
        //targetY = checkBoundY(targetY);
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

    public void release(int targetX, int targetY) {
        long now = SystemClock.uptimeMillis();
        //targetX = checkBoundX(targetX);
        //targetY = checkBoundY(targetY);
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

    public void move(int toX, int toY) {
        long now = SystemClock.uptimeMillis();
        //toX = checkBoundX(toX);
        //toY = checkBoundY(toY);
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

    public void execute(Association association) {
        switch (association.action) {
            case TAP:
                touch(association.x, association.y);
                sleep(10);
                release(association.x, association.y);
                break;
            case SWIPE_UP:
                touch(association.x, association.y);
                sleep(10);
                move(association.x, association.y - 100);
                sleep(10);
                release(association.x, association.y - 100);
                break;
            case SWIPE_DOWN:
                touch(association.x, association.y);
                sleep(10);
                move(association.x, association.y + 100);
                sleep(10);
                release(association.x, association.y + 100);
                break;
            case SWIPE_LEFT:
                touch(association.x, association.y);
                sleep(10);
                move(association.x - 100, association.y);
                sleep(10);
                release(association.x - 100, association.y);
                break;
            case SWIPE_RIGHT:
                touch(association.x, association.y);
                sleep(10);
                move(association.x + 100, association.y);
                sleep(10);
                release(association.x + 100, association.y);
                break;
            case LONG_TAP:
                touch(association.x, association.y);
                sleep(2000);
                release(association.x, association.y);
                break;
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

