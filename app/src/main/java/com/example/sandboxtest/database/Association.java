package com.example.sandboxtest.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.sandboxtest.actionsConfigurator.Action;
import com.example.sandboxtest.actionsConfigurator.Event;

@Entity
public class Association {
    @PrimaryKey @NonNull
    public String applicationPackage;
    @PrimaryKey @NonNull
    public Event event;
    @NonNull
    public Action action;
    @NonNull
    public int x;
    @NonNull
    public int y;
    public int radius;
}
