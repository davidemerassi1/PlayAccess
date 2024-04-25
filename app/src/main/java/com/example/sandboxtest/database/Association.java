package com.example.sandboxtest.database;

import android.app.Instrumentation;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.sandboxtest.actionsConfigurator.Action;
import com.example.sandboxtest.actionsConfigurator.ActionExecutor;
import com.example.sandboxtest.actionsConfigurator.Event;
import com.example.sandboxtest.actionsConfigurator.OverlayView;

@Entity(primaryKeys = {"applicationPackage", "event"})
public class Association {
    @NonNull
    public String applicationPackage;
    @NonNull
    public Event event;
    @NonNull
    public Action action;
    @NonNull
    public int x;
    @NonNull
    public int y;
    public Integer radius;

    public Association(String applicationPackage, Event event, Action action, int x, int y, Integer radius) {
        this.applicationPackage = applicationPackage;
        this.event = event;
        this.action = action;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }


    public void execute(ActionExecutor executor, Instrumentation instrumentation) {
        switch (action) {
            case TAP:
                executor.touch(instrumentation, x, y);
                executor.release(instrumentation, x, y);
                break;
            case SWIPE_UP:
                executor.touch(instrumentation, x, y);
                executor.move(instrumentation, x, y - 100);
                executor.release(instrumentation, x, y - 100);
                break;
            case SWIPE_DOWN:
                executor.touch(instrumentation, x, y);
                executor.move(instrumentation, x, y + 100);
                executor.release(instrumentation, x, y + 100);
                break;
            case SWIPE_LEFT:
                executor.touch(instrumentation, x, y);
                executor.move(instrumentation, x - 100, y);
                executor.release(instrumentation, x - 100, y);
                break;
            case SWIPE_RIGHT:
                executor.touch(instrumentation, x, y);
                executor.move(instrumentation, x + 100, y);
                executor.release(instrumentation, x + 100, y);
                break;
        }
    }
}
