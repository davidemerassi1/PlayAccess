package it.unimi.di.ewlab.iss.common.database;

public enum CameraAction {
    SMILE("Smile", false, -1),
    OTHER("Other", false, -2),
    FACE_MOVEMENT("Face movement", true, -3);

    private boolean joystickAction;
    private String name;
    private int tag;

    CameraAction(String name, boolean joystickAction, int tag) {
        this.joystickAction = joystickAction;
        this.name = name;
        this.tag = tag;
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

    public int getTag() {
        return tag;
    }

    public static CameraAction valueOf(int tag) {
        for (CameraAction event : CameraAction.values()) {
            if (event.getTag() == tag) {
                return event;
            }
        }
        return null;
    }
}
