package it.unimi.di.ewlab.iss.common.model;


import androidx.annotation.NonNull;

import java.io.Serializable;

public class Event implements Serializable {

    private String name;
    private EventType type;

    private double x;
    private double y;

    private boolean portrait;

    public Event() {

    }

    public Event(String name, EventType type, double x, double y, boolean portrait) {
        this.name = name.trim();
        this.type = type;
        this.x = x;
        this.y = y;

        this.portrait = portrait;
    }

    public Event(String name, EventType type, double x, double y) {
        this.name = name.trim();
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public EventType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean getPortrait() {
        return portrait;
    }

    public void setPortrait(boolean portrait) {
        this.portrait = portrait;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setType(EventType type) {
        this.type = type;
    }


    public boolean equals(Object other) {

        if (!(other instanceof Event)) {
            return false;
        }

        Event otherEvent = (Event) other;
        return this.getName().equals(otherEvent.getName());
    }

    @Override
    @NonNull
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", portrait=" + portrait +
                '}';
    }
}
