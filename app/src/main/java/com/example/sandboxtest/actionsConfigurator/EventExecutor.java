package com.example.sandboxtest.actionsConfigurator;

import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.example.sandboxtest.database.Association;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class EventExecutor {
    private int statusBarHeight;
    private Instrumentation instrumentation;
    private ArrayList<MotionEvent.PointerProperties> pointerProperties;
    private ArrayList<MotionEvent.PointerCoords> pointerCoords;
    private ArrayList<Association> actions;
    private Map<Association, Integer> x2d = new HashMap<>();
    int currentId = 0;

    public EventExecutor(Context context) {
        statusBarHeight = getStatusBarHeight(context);
        instrumentation = new Instrumentation();
        pointerProperties = new ArrayList<>();
        pointerCoords = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public void touch(int targetX, int targetY, Association association) {
        MotionEvent.PointerProperties properties = new MotionEvent.PointerProperties();
        properties.id = currentId;
        currentId++;
        properties.toolType = MotionEvent.TOOL_TYPE_FINGER;
        pointerProperties.add(properties);
        MotionEvent.PointerCoords coords = createCoords(targetX, targetY + statusBarHeight);
        pointerCoords.add(coords);
        actions.add(association);
        Log.d("ActionExecutor", "properties size: " + pointerProperties.size() + "coords size: " + pointerCoords.size() + "events size: " + actions.size());
        MotionEvent.PointerProperties[] propertiesArray = pointerProperties.toArray(new MotionEvent.PointerProperties[0]);
        MotionEvent.PointerCoords[] coordsArray = pointerCoords.toArray(new MotionEvent.PointerCoords[0]);
        long now = SystemClock.uptimeMillis();
        MotionEvent touchEvent;
        switch (pointerCoords.size()) {
            case 1:
                touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 1, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
                instrumentation.sendPointerSync(touchEvent);
                break;
            case 2:
                /*touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 1, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
                instrumentation.sendPointerSync(touchEvent);*/
                touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_POINTER_2_DOWN, 2, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
                instrumentation.sendPointerSync(touchEvent);
                break;
        }
    }

    public void release(Association association) {
        Log.d("EventExecutor", "Releasing event");
        if (!actions.contains(association)) {
            Log.d("ActionExecutor", "Event not in progress");
            return;
        }
        long now = SystemClock.uptimeMillis();
        switch (pointerProperties.size()) {
            case 1:
                MotionEvent.PointerProperties[] propertiesArray = pointerProperties.toArray(new MotionEvent.PointerProperties[0]);
                MotionEvent.PointerCoords[] coordsArray = pointerCoords.toArray(new MotionEvent.PointerCoords[0]);
                MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, 1, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
                instrumentation.sendPointerSync(touchEvent);
                pointerProperties.remove(0);
                pointerCoords.remove(0);
                actions.remove(0);
                break;
            case 2:
                if (actions.get(0) == association) {
                    propertiesArray = pointerProperties.toArray(new MotionEvent.PointerProperties[0]);
                    coordsArray = pointerCoords.toArray(new MotionEvent.PointerCoords[0]);
                    touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, 2, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
                    instrumentation.sendPointerSync(touchEvent);
                    pointerProperties.remove(0);
                    pointerCoords.remove(0);
                    actions.remove(0);
                    propertiesArray = pointerProperties.toArray(new MotionEvent.PointerProperties[0]);
                    coordsArray = pointerCoords.toArray(new MotionEvent.PointerCoords[0]);
                    touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 1, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
                    instrumentation.sendPointerSync(touchEvent);
                } else {
                    propertiesArray = pointerProperties.toArray(new MotionEvent.PointerProperties[0]);
                    coordsArray = pointerCoords.toArray(new MotionEvent.PointerCoords[0]);
                    touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_POINTER_2_UP, 2, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
                    instrumentation.sendPointerSync(touchEvent);
                    pointerProperties.remove(1);
                    pointerCoords.remove(1);
                    actions.remove(1);
                }
                break;
        }
    }

    public void move(int toX, int toY, Association association) {
        Log.d("EventExecutor", "Moving event");
        long now = SystemClock.uptimeMillis();
        if (actions.size() > 0 && actions.get(0).equals(association))
            pointerCoords.set(0, createCoords(toX, toY + statusBarHeight));
        else if (actions.size() > 1 && actions.get(1).equals(association))
            pointerCoords.set(1, createCoords(toX, toY + statusBarHeight));
        else return;
        MotionEvent.PointerProperties[] propertiesArray = pointerProperties.toArray(new MotionEvent.PointerProperties[0]);
        MotionEvent.PointerCoords[] coordsArray = pointerCoords.toArray(new MotionEvent.PointerCoords[0]);
        MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, coordsArray.length, propertiesArray, coordsArray, 0, 0, 1, 1, 0, 0, 0, 0);
        instrumentation.sendPointerSync(touchEvent);
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
        if (actions.contains(association.action)) {
            Log.d("ActionExecutor", "Event already in progress");
            return;
        }
        new Thread(() -> {
            switch (association.event) {
                case TAP:
                    touch(association.x, association.y, association);
                    sleep(10);
                    release(association);
                    break;
                case SWIPE_UP:
                    touch(association.x, association.y, association);
                    sleep(10);
                    move(association.x, association.y - 100, association);
                    sleep(10);
                    release(association);
                    break;
                case SWIPE_DOWN:
                    touch(association.x, association.y, association);
                    sleep(10);
                    move(association.x, association.y + 100, association);
                    sleep(10);
                    release(association);
                    break;
                case SWIPE_LEFT:
                    touch(association.x, association.y, association);
                    sleep(10);
                    move(association.x - 100, association.y, association);
                    sleep(10);
                    release(association);
                    break;
                case SWIPE_RIGHT:
                    touch(association.x, association.y, association);
                    sleep(10);
                    move(association.x + 100, association.y, association);
                    sleep(10);
                    release(association);
                    break;
                case LONG_TAP:
                    touch(association.x, association.y, association);
                    sleep(2000);
                    release(association);
                    break;
            }
        }).start();
    }

    /**
     * Esegue movimento 2d (joystick)
     *
     * @param association
     * @param x           coordinata x in [-1, 1]
     * @param y           coordinata y in [-1, 1]
     */
    public void execute2d(Association association, float x, float y) {
        new Thread(() -> {
            if (actions.contains(association)) {
                if (Math.abs(x) > 0.2 || Math.abs(y) > 0.2) {
                    move((int) (x * association.radius) + association.x, (int) (y * association.radius) + association.y, association);
                } else {
                    release(association);
                }
            } else {
                if (Math.abs(x) > 0.2 || Math.abs(y) > 0.2) {
                    touch(association.x, association.y, association);
                    move((int) (x * association.radius) + association.x, (int) (y * association.radius) + association.y, association);
                }
            }
        }).start();
    }

    public void execute1d(Association association, Action1D action1d) {
        new Thread(() -> {
            switch (action1d) {
                case MOVE_LEFT:
                    if (!x2d.containsKey(association)) {
                        touch(association.x, association.y, association);
                        x2d.put(association, association.x);
                    }
                    x2d.put(association, Math.max(x2d.get(association) - 100, association.x - association.radius));
                    move(x2d.get(association), association.y, association);
                    break;
                case MOVE_RIGHT:
                    if (!x2d.containsKey(association)) {
                        touch(association.x, association.y, association);
                        x2d.put(association, association.x);
                    }
                    x2d.put(association, Math.min(x2d.get(association) + 100, association.x + association.radius));
                    move(x2d.get(association), association.y, association);
                    break;
                case RESET:
                    if (association.resetToStart)
                        move(association.x, association.y, association);
                    release(association);
                    x2d.remove(association);
                    break;
            }
        }).start();
    }

    public void releaseAll() {
        while (actions.size() > 0) {
            Log.d("ActionExecutor", "Releasing event");
            release(actions.get(0));
        }
        x2d.clear();
    }

    private MotionEvent.PointerCoords createCoords(int x, int y) {
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        coords.x = x;
        coords.y = y;
        coords.pressure = 1;
        coords.size = 1;
        return coords;
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public enum Action1D {
        MOVE_LEFT, MOVE_RIGHT, RESET
    }
}

