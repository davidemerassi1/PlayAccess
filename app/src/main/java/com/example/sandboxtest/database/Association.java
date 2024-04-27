package com.example.sandboxtest.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

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
}
