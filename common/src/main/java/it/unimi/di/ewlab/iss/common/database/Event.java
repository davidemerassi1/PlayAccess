package it.unimi.di.ewlab.iss.common.database;

public enum Event {
    TAP,
    SWIPE_UP,
    SWIPE_DOWN,
    SWIPE_LEFT,
    SWIPE_RIGHT,
    LONG_TAP,
    JOYSTICK,
    MONODIMENSIONAL_SLIDING,
    TAP_ON_OFF;

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
            case MONODIMENSIONAL_SLIDING:
                return "Monodimensional sliding";
            case TAP_ON_OFF:
                return "Long tap on/off";
            default:
                return "Unknown";
        }
    }
}

