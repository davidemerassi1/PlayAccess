package com.example.sandboxtest.database;

public enum Action {
    TAP,
    SWIPE_UP,
    SWIPE_DOWN,
    SWIPE_LEFT,
    SWIPE_RIGHT,
    LONG_TAP,
    JOYSTICK;

    public String toString() {
        switch (this) {
            case TAP:
                return "Tap";
            case SWIPE_UP:
                return "Swipe up";
            case SWIPE_DOWN:
                return "Swipe down";
            case SWIPE_LEFT:
                return "Swipe left";
            case SWIPE_RIGHT:
                return "Swipe right";
            case LONG_TAP:
                return "Long tap";
            case JOYSTICK:
                return "Joystick";
            default:
                return "Unknown";
        }
    }
}

