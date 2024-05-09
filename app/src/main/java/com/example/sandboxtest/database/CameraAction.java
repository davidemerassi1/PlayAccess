package com.example.sandboxtest.database;

public enum CameraAction {
    SMILE("Smile", false),
    OTHER("Other", false),
    FACE_MOVEMENT("Face movement", true);

    private boolean joystickAction;
    private String name;

    CameraAction(String name, boolean joystickAction) {
        this.joystickAction = joystickAction;
        this.name = name;
    }

    public boolean isJoystickAction() {
        return joystickAction;
    }

    public String getName() {
        return name;
    }

    public static boolean exists(String name) {
        for (CameraAction event : CameraAction.values()) {
            if (event.toString().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
