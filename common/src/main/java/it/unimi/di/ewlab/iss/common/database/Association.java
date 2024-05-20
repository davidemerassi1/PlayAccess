package it.unimi.di.ewlab.iss.common.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

@Entity(primaryKeys = {"applicationPackage", "action"})
public class Association {
    @NonNull
    public String applicationPackage;

    /*
    >0: keycode del tasto premuto (definito da android)
    0: joystick fisico
    <0: face action
    */
    @NonNull
    public Action action;
    @NonNull
    public Event event;
    @NonNull
    public int x;
    @NonNull
    public int y;
    public Integer radius;
    public Action additionalAction1;
    public Action additionalAction2;
    public Boolean resetToStart;

    public Association(String applicationPackage, Action action, Event event, int x, int y, Integer radius, Action additionalAction1, Action additionalAction2, Boolean resetToStart) {
        this.applicationPackage = applicationPackage;
        this.event = event;
        this.action = action;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.additionalAction1 = additionalAction1;
        this.additionalAction2 = additionalAction2;
        this.resetToStart = resetToStart;
    }
}
