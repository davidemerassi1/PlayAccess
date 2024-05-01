package com.example.sandboxtest.database;

public enum CameraEvent {
    SMILE("Smile", false),
    OTHER("Other", false),
    FACE_MOVEMENT("Face movement", true);

    private boolean joystickEvent;
    private String name;

    CameraEvent(String name, boolean joystickEvent) {
        this.joystickEvent = joystickEvent;
        this.name = name;
    }

    public boolean isJoystickEvent() {
        return joystickEvent;
    }

    public String getName() {
        return name;
    }

    public static boolean exists(String name) {
        for (CameraEvent event : CameraEvent.values()) {
            if (event.toString().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
