package com.example.accessibilityservice;

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

import com.example.actionsrecognizer.facialexpressionactionsrecognizer.ActionListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import it.unimi.di.ewlab.iss.common.database.Association;
import it.unimi.di.ewlab.iss.common.database.AssociationDao;
import it.unimi.di.ewlab.iss.common.database.Event;
import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

import java.util.ArrayList;
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

    public EventExecutor(MyAccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
        statusBarHeight = getStatusBarHeight(accessibilityService);
        associationsDb = MainModel.getInstance().getAssociationsDb().getDao();

        associations.observeForever(associations -> {
            if (associations != null) {
                Log.d("EventExecutor", "Associations changed");
                //releaseAll();

                boolean needCamera = false;
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
                s = new StrokeDescription(path, 0, 10);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                break;
            case SWIPE_UP:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x, association.y - 500);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                break;
            case SWIPE_DOWN:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x, association.y + 500);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                break;
            case SWIPE_LEFT:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x - 500, association.y);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                break;
            case SWIPE_RIGHT:
                path = new Path();
                path.moveTo(association.x, association.y);
                path.lineTo(association.x + 500, association.y);
                s = new StrokeDescription(path, 0, 300);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                break;
            case LONG_TAP:
                path = new Path();
                path.moveTo(association.x, association.y);
                s = new StrokeDescription(path, 0, 1500);
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                break;
            case TAP_ON_OFF:
                path = new Path();
                path.moveTo(association.x, association.y);
                s = new StrokeDescription(path, 0, 1, true);
                inProgress.put(association, new StrokeInProgress(s, association.x, association.y));
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
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
        boolean activeMovement = Math.abs(x) > 0.3 || Math.abs(y) > 0.3;
        if (!inProgress.containsKey(association)) {
            if (activeMovement) {
                Path path = new Path();
                path.moveTo(association.x, association.y);
                int toX = (int) (x * association.radius) + association.x;
                int toY = (int) (y * association.radius) + association.y;
                path.lineTo(toX, toY);
                StrokeDescription s = new StrokeDescription(path, 0, 10, true);
                inProgress.put(association, new StrokeInProgress(s, toX, toY));
                GestureDescription g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
            }
        } else {
            if (activeMovement) {
                Path path = new Path();
                StrokeInProgress strokeInProgress = inProgress.get(association);
                path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                int toX = (int) (x * association.radius) + association.x;
                int toY = (int) (y * association.radius) + association.y;
                path.lineTo(toX, toY);
                StrokeDescription s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 10, true);
                inProgress.put(association, new StrokeInProgress(s, toX, toY));
                GestureDescription g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
            } else {
                Path path = new Path();
                StrokeInProgress strokeInProgress = inProgress.get(association);
                path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                StrokeDescription s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 1, false);
                inProgress.remove(association);
                GestureDescription g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
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
                } else {
                    StrokeInProgress strokeInProgress = inProgress.get(association);
                    path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                    int toX = Math.max(strokeInProgress.x() - 100, association.x - association.radius);
                    path.lineTo(toX, strokeInProgress.y());
                    s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 100, true);
                    inProgress.put(association, new StrokeInProgress(s, toX, strokeInProgress.y()));
                }
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                moving1d = true;
                sleep(100);

                handler.post(new Runnable() {
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
                        accessibilityService.dispatchGesture(g1, null, null);
                        handler.postDelayed(this, 20);
                    }
                });

                break;
            case MOVE_RIGHT:
                if (!inProgress.containsKey(association)) {
                    path.moveTo(association.x, association.y);
                    int toX = Math.min(association.x + 100, association.x + association.radius);
                    path.lineTo(toX, association.y);
                    s = new StrokeDescription(path, 0, 100, true);
                    inProgress.put(association, new StrokeInProgress(s, toX, association.y));
                } else {
                    StrokeInProgress strokeInProgress = inProgress.get(association);
                    path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                    int toX = Math.min(strokeInProgress.x() + 100, association.x + association.radius);
                    path.lineTo(toX, strokeInProgress.y());
                    s = inProgress.get(association).strokeDescription().continueStroke(path, 0, 100, true);
                    inProgress.put(association, new StrokeInProgress(s, toX, strokeInProgress.y()));
                }
                g = new GestureDescription.Builder().addStroke(s).build();
                accessibilityService.dispatchGesture(g, null, null);
                moving1d = true;
                sleep(100);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!moving1d) {
                            handler.removeCallbacks(this);
                            return;
                        }
                        StrokeInProgress strokeInProgress = inProgress.get(association);
                        Path path1 = new Path();
                        path1.moveTo(strokeInProgress.x(), strokeInProgress.y());
                        int toX = Math.max(strokeInProgress.x() + 20, association.x - association.radius);
                        path1.lineTo(toX, strokeInProgress.y());
                        StrokeDescription s1 = inProgress.get(association).strokeDescription().continueStroke(path1, 0, 20, true);
                        inProgress.put(association, new StrokeInProgress(s1, toX, strokeInProgress.y()));
                        GestureDescription g1 = new GestureDescription.Builder().addStroke(s1).build();
                        accessibilityService.dispatchGesture(g1, null, null);
                        handler.postDelayed(this, 20);
                    }
                });

                break;
            case RESET:
                if (inProgress.containsKey(association)) {
                    Log.d("ActionExecutor", "Reset to start: " + association.resetToStart);
                    if (association.resetToStart) {
                        StrokeInProgress strokeInProgress = inProgress.get(association);
                        path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                        path.lineTo(association.x, association.y);
                        s = strokeInProgress.strokeDescription().continueStroke(path, 0, 400, false);

                    } else {
                        StrokeInProgress strokeInProgress = inProgress.get(association);
                        path.moveTo(strokeInProgress.x(), strokeInProgress.y());
                        s = strokeInProgress.strokeDescription().continueStroke(path, 0, 1, false);
                    }
                    g = new GestureDescription.Builder().addStroke(s).build();
                    accessibilityService.dispatchGesture(g, null, null);
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
                accessibilityService.dispatchGesture(g, null, null);
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
        Log.d("EventExecutor", "2d movement: " + action.getName() + " x: " + x + " y: " + y);
        for (Association a : associations.getValue()) {
            if (a.action.equals(action) && a.event == Event.JOYSTICK)
                execute2d(a, x, y);
        }
    }

    public void changeGame(String packageName) {
        Log.d("EventExecutor", "Changing game");
        new Thread(() -> {
            Association[] associationsArray = associationsDb.getAssociations(packageName);
            for (Association a : associationsArray)
                a.y += statusBarHeight;
            associations.postValue(associationsArray);
            resume();
        }).start();
    }

    private void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public enum Action1D {
        MOVE_LEFT, MOVE_RIGHT, RESET
    }
}

