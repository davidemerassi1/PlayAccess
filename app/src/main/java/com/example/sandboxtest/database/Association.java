package com.example.sandboxtest.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

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
    public int action;
    @NonNull
    public Event event;
    @NonNull
    public int x;
    @NonNull
    public int y;
    public Integer radius;
    public Integer additionalAction1;
    public Integer additionalAction2;

    public Association(String applicationPackage, int action, Event event, int x, int y, Integer radius, Integer additionalAction1, Integer additionalAction2) {
        this.applicationPackage = applicationPackage;
        this.event = event;
        this.action = action;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.additionalAction1 = additionalAction1;
        this.additionalAction2 = additionalAction2;
    }
}
