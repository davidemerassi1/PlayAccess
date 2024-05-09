package com.example.sandboxtest.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"applicationPackage", "event"})
public class Association {
    @NonNull
    public String applicationPackage;
    @NonNull
    public String action;
    @NonNull
    public Event event;
    @NonNull
    public int x;
    @NonNull
    public int y;
    public Integer radius;
    public String additionalAction1;
    public String additionalAction2;

    public Association(String applicationPackage, String action, Event event, int x, int y, Integer radius) {
        this.applicationPackage = applicationPackage;
        this.event = event;
        this.action = action;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}
