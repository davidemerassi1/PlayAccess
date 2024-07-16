package it.unimi.di.ewlab.iss.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.accessibilityservice.GestureDescription.StrokeDescription;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import it.unimi.di.ewlab.iss.actionsrecognizer.ActionListener;
import it.unimi.di.ewlab.iss.gamesconfigurator.TouchIndicatorView;

import org.checkerframework.checker.nullness.qual.NonNull;

import it.unimi.di.ewlab.iss.common.database.Association;
import it.unimi.di.ewlab.iss.common.database.AssociationDao;
import it.unimi.di.ewlab.iss.common.database.Event;
import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

import java.util.HashMap;
import java.util.Map;

public class EventExecutor implements ActionListener {
    private int statusBarHeight;
    private Map<Association, StrokeInProgress> inProgress = new HashMap<>();
    private AssociationDao associationsDb;
    private MutableLiveData<Association[]> associations = new MutableLiveData<>();
    private MyAccessibilityService accessibilityService;
    Handler handler = new Handler(Looper.getMainLooper());
    private boolean moving1d = false;
    private boolean paused = false;
    private TouchIndicatorView touchIndicatorView;
    private boolean needCamera;
    private AccessibilityService.GestureResultCallback gestureResultCallback = new AccessibilityService.GestureResultCallback() {
        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);
            Log.d("EventExecutor", "Gesture completed");
        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            touchIndicatorView.clear();
            moving1d = false;
            inProgress.clear();

            Log.d("EventExecutor", "Gesture cancelled");
            super.onCancelled(gestureDescription);
        }
    };

    public EventExecutor(MyAccessibilityService accessibilityService, TouchIndicatorView touchIndicatorView) {
        this.accessibilityService = accessibilityService;
        this.touchIndicatorView = touchIndicatorView;
        statusBarHeight = getStatusBarHeight(accessibilityService);
        associationsDb = MainModel.getInstance().getAssociationsDb().getDao();

        associations.observeForever(associations -> {
            if (associations != null) {
                Log.d("EventExecutor", "Associations changed");
                //releaseAll();

                needCamera = false;
                for (Association association : associations) {
                    if (association.action.getActionType() == Action.ActionType.FACIAL_EXPRESSION)
                        needCamera = true;
                    if (association.event == Event.MONODIMENSIONAL_SLIDING) {
                        if (association.additionalAction1.getActionType() == Action.ActionType.FACIAL_EXPRESSION || association.additionalAction2.getActionType() == Action.ActionType.FACIAL_EXPRESSION)
                            needCamera = true;
                    }
                }
                accessibilityService.setCameraNeeded(needCamera);
            }
        });
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
        Log.d("ActionExecutor", "Executing event");
        Path path;
        StrokeDescription s;
        GestureDescription g;
        switch (association.event) {
            case TAP:
                path = new Path();
                path.moveTo(association.x, association.y);
                s = new StrokeDescription(path, 0, 100);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.onTouch(association.x, association.y);
                handler.postDelayed(() -> touchIndicatorView.clear(), 100);
                break;
            case SWIPE_UP:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x, association.y - 500);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.drawSwipe(association.x, association.y, 500, TouchIndicatorView.SwipeDirection.UP, 300, true);
                break;
            case SWIPE_DOWN:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x, association.y + 500);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                touchIndicatorView.drawSwipe(association.x, association.y, 500, TouchIndicatorView.SwipeDirection.DOWN, 300, true);
                break;
            case SWIPE_LEFT:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x - 500, association.y);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.drawSwipe(association.x, association.y, 500, TouchIndicatorView.SwipeDirection.LEFT, 300, true);
                break;
            case SWIPE_RIGHT:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x + 500, association.y);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.drawSwipe(association.x, association.y, 500, TouchIndicatorView.SwipeDirection.RIGHT, 300, true);
                break;
            case LONG_TAP:
                path = new Path();
                path.moveTo(association.x, association.y);
                s = new StrokeDescription(path, 0, 1500);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.onTouch(association.x, association.y);
                handler.postDelayed(() -> touchIndicatorView.clear(), 1500);
                break;
            case TAP_ON_OFF:
                path = new Path();
                path.moveTo(association.x, association.y);
                s = new StrokeDescription(path, 0, 1, true);
                inProgress.put(association, new StrokeInProgress(s, association.x, association.y));
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.onTouch(association.x, association.y);
                break;
        }
    }

    /**
     * Esegue movimento 2d (joystick)
     *
     * @param association
     * @param x           coordinata x in [-1, 1]
     * @param y           coordinata y in [-1, 1]
     */
    public void execute2d(Association association, float x, float y) {
        Log.d("EventExecutor", "2d movement: x: " + x + " y: " + y);
        boolean activeMovement = Math.abs(x) > 0.3 || Math.abs(y) > 0.3;
        float xMax = (float) (x * Math.sqrt(1 - y * y / 2));
        float yMax = (float) (y * Math.sqrt(1 - x * x / 2));
        if (Math.abs(x) > Math.abs(xMax))
            x = xMax;
        if (Math.abs(y) > Math.abs(yMax))
            y = yMax;
        if (!inProgress.containsKey(association)) {
            if (activeMovement) {
                Path path = new Path();
                path.moveTo(association.x, association.y);
                int toX = (int) (x * association.radius) + association.x;
                int toY = (int) (y * association.radius) + association.y;
                path.lineTo(toX, toY);
                StrokeDescription s = new StrokeDescription(path, 0, 1, true);
                inProgress.put(association, new StrokeInProgress(s, toX, toY));
                Log.d("EventExecutor", "Starting " + s +" from " + association.x + " " + association.y + " to " + toX + " " + toY);
                GestureDescription g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.onTouch(toX, toY);
            }
        } else {
            if (activeMovement) {
                Path path = new Path();
                StrokeInProgress strokeInProgress = inProgress.get(association);
                path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                int toX = (int) (x * association.radius) + association.x;
                int toY = (int) (y * association.radius) + association.y;
                path.lineTo(toX, toY);
                Log.d("EventExecutor", "Continuing " + strokeInProgress.strokeDescription() + " from: " + strokeInProgress.x() + " " + strokeInProgress.y() + " to " + toX + " " + toY);
                StrokeDescription s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 1, true);
                inProgress.put(association, new StrokeInProgress(s, toX, toY));
                GestureDescription g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.onTouch(toX, toY);
            } else {
                Path path = new Path();
                StrokeInProgress strokeInProgress = inProgress.get(association);
                path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                Log.d("EventExecutor", "Ending " + strokeInProgress.strokeDescription() + " at: " + strokeInProgress.x() + " " + strokeInProgress.y());
                StrokeDescription s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 1, false);
                inProgress.remove(association);
                GestureDescription g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.clear();
            }
        }
    }

    public void execute1d(Association association, Action1D action1d) {
        Path path = new Path();
        StrokeDescription s;
        GestureDescription g;
        switch (action1d) {
            case MOVE_LEFT:
                if (!inProgress.containsKey(association)) {
                    path.moveTo(association.x, association.y);
                    int toX = Math.max(association.x - 100, association.x - association.radius);
                    path.lineTo(toX, association.y);
                    s = new StrokeDescription(path, 0, 100, true);
                    inProgress.put(association, new StrokeInProgress(s, toX, association.y));
                    touchIndicatorView.drawSwipe(association.x, association.y, Math.abs(toX-association.x), TouchIndicatorView.SwipeDirection.LEFT, 100, false);
                } else {
                    StrokeInProgress strokeInProgress = inProgress.get(association);
                    path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                    int toX = Math.max(strokeInProgress.x() - 100, association.x - association.radius);
                    path.lineTo(toX, strokeInProgress.y());
                    s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 100, true);
                    inProgress.put(association, new StrokeInProgress(s, toX, strokeInProgress.y()));
                    touchIndicatorView.drawSwipe(strokeInProgress.x(), association.y, Math.abs(toX-strokeInProgress.x()), TouchIndicatorView.SwipeDirection.LEFT, 100, false);
                }
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                moving1d = true;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!moving1d) {
                            handler.removeCallbacks(this);
                            return;
                        }
                        StrokeInProgress strokeInProgress = inProgress.get(association);
                        Path path1 = new Path();
                        path1.moveTo(strokeInProgress.x(), strokeInProgress.y());
                        int toX = Math.max(strokeInProgress.x() - 20, association.x - association.radius);
                        path1.lineTo(toX, strokeInProgress.y());
                        StrokeDescription s1 = inProgress.get(association).strokeDescription().continueStroke(path1, 0, 20, true);
                        inProgress.put(association, new StrokeInProgress(s1, toX, strokeInProgress.y()));
                        GestureDescription g1 = new GestureDescription.Builder().addStroke(s1).build();
                        accessibilityService.dispatchGesture(g1, gestureResultCallback, null);
                        touchIndicatorView.onTouch(toX, strokeInProgress.y());
                        handler.postDelayed(this, 20);
                    }
                }, 100);

                break;
            case MOVE_RIGHT:
                if (!inProgress.containsKey(association)) {
                    path.moveTo(association.x, association.y);
                    int toX = Math.min(association.x + 100, association.x + association.radius);
                    path.lineTo(toX, association.y);
                    s = new StrokeDescription(path, 0, 100, true);
                    inProgress.put(association, new StrokeInProgress(s, toX, association.y));
                    touchIndicatorView.drawSwipe(association.x, association.y, Math.abs(toX-association.x), TouchIndicatorView.SwipeDirection.RIGHT, 100, false);
                } else {
                    StrokeInProgress strokeInProgress = inProgress.get(association);
                    path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                    int toX = Math.min(strokeInProgress.x() + 100, association.x + association.radius);
                    path.lineTo(toX, strokeInProgress.y());
                    s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 100, true);
                    inProgress.put(association, new StrokeInProgress(s, toX, strokeInProgress.y()));
                    touchIndicatorView.drawSwipe(strokeInProgress.x(), association.y, Math.abs(toX-strokeInProgress.x()), TouchIndicatorView.SwipeDirection.RIGHT, 100, false);
                }
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                moving1d = true;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!moving1d) {
                            handler.removeCallbacks(this);
                            return;
                        }
                        StrokeInProgress strokeInProgress = inProgress.get(association);
                        Path path1 = new Path();
                        path1.moveTo(strokeInProgress.x(), strokeInProgress.y());
                        int toX = Math.min(strokeInProgress.x() + 20, association.x + association.radius);
                        path1.lineTo(toX, strokeInProgress.y());
                        StrokeDescription s1 = inProgress.get(association).strokeDescription().continueStroke(path1, 0, 20, true);
                        inProgress.put(association, new StrokeInProgress(s1, toX, strokeInProgress.y()));
                        GestureDescription g1 = new GestureDescription.Builder().addStroke(s1).build();
                        accessibilityService.dispatchGesture(g1, gestureResultCallback, null);
                        touchIndicatorView.onTouch(toX, strokeInProgress.y());
                        handler.postDelayed(this, 20);
                    }
                }, 100);

                break;
            case RESET:
                if (inProgress.containsKey(association)) {
                    Log.d("ActionExecutor", "Reset to start: " + association.resetToStart);
                    if (association.resetToStart) {
                        StrokeInProgress strokeInProgress = inProgress.get(association);
                        path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                        path.lineTo(association.x, association.y);
                        s = strokeInProgress.strokeDescription().continueStroke(path, 0, 300, false);
                        if (strokeInProgress.x() < association.x) {
                            touchIndicatorView.drawSwipe(strokeInProgress.x(), association.y, Math.abs(association.x-strokeInProgress.x()), TouchIndicatorView.SwipeDirection.RIGHT, 300, true);
                        } else {
                            touchIndicatorView.drawSwipe(strokeInProgress.x(), association.y, Math.abs(association.x-strokeInProgress.x()), TouchIndicatorView.SwipeDirection.LEFT, 300, true);
                        }
                    } else {
                        StrokeInProgress strokeInProgress = inProgress.get(association);
                        path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                        s = strokeInProgress.strokeDescription().continueStroke(path, 0, 1, false);
                        touchIndicatorView.clear();
                    }
                    g = new GestureDescription.Builder().addStroke(s).build();
                    accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                    inProgress.remove(association);
                }
                break;
        }
    }

    public void stopExecuting(Association association, Action1D action1d) {
        switch (association.event) {
            case TAP_ON_OFF -> {
                Path path = new Path();
                path.moveTo(association.x, association.y);
                StrokeDescription s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 1, false);
                inProgress.remove(association);
                GestureDescription g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, gestureResultCallback, null);
                touchIndicatorView.clear();
            }
            case MONODIMENSIONAL_SLIDING -> {
                moving1d = false;
            }
        }
    }

    /*

    public void releaseAll() {
        while (actions.size() > 0) {
            if (actions.get(0).resetToStart != null && actions.get(0).resetToStart)
                x2d.remove(actions.get(0));
            Log.d("ActionExecutor", "Releasing event");
            release(actions.get(0));
        }
        x2d.clear();
    }
    */

    @Override
    public void onActionStarts(@NonNull Action action) {
        if (paused) return;
        for (Association a : associations.getValue()) {
            if (a.event == Event.MONODIMENSIONAL_SLIDING) {
                if (a.action.equals(action))
                    execute1d(a, Action1D.MOVE_LEFT);
                else if (a.additionalAction1.equals(action))
                    execute1d(a, Action1D.MOVE_RIGHT);
                else if (a.additionalAction2.equals(action))
                    execute1d(a, Action1D.RESET);
            } else if (a.action.equals(action))
                execute(a);
        }
    }

    @Override
    public void onActionEnds(@NonNull Action action) {
        if (paused) return;
        for (Association a : associations.getValue()) {
            if (a.event == Event.MONODIMENSIONAL_SLIDING) {
                if (a.action.equals(action))
                    stopExecuting(a, Action1D.MOVE_LEFT);
                else if (a.additionalAction1.equals(action))
                    stopExecuting(a, Action1D.MOVE_RIGHT);
                else if (a.additionalAction2.equals(action))
                    stopExecuting(a, Action1D.RESET);
            } else if (a.action.equals(action))
                stopExecuting(a, null);
        }
    }

    @Override
    public void on2dMovement(Action action, float x, float y) {
        if (paused) return;
        for (Association a : associations.getValue()) {
            if (a.action.equals(action) && a.event == Event.JOYSTICK)
                execute2d(a, x, y);
        }
    }

    public void changeGame(String packageName) {
        Log.d("EventExecutor", "Changing game");
        touchIndicatorView.clear();
        new Thread(() -> {
            Association[] associationsArray = associationsDb.getAssociations(packageName);
            for (Association a : associationsArray)
                a.y += statusBarHeight;
            associations.postValue(associationsArray);
            resume();
        }).start();
    }

    public void pause() {
        paused = true;
        accessibilityService.setCameraNeeded(false);
    }

    public void resume() {
        paused = false;
        handler.post(() -> accessibilityService.setCameraNeeded(needCamera));
    }

    public enum Action1D {
        MOVE_LEFT, MOVE_RIGHT, RESET
    }
}

