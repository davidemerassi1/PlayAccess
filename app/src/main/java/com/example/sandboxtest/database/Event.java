package com.example.sandboxtest.database;

public enum Event {
    SMILE("Smile", false),
    EVENTO2("Evento2", false),
    JOYSTICKEVENT("JoystickEvent", true);

    private String name;
    private boolean joystickEvent;
    Event(String name, boolean joystickEvent) {
        this.name = name;
        this.joystickEvent = joystickEvent;
    }

    public String getName() {
        return name;
    }

    public boolean isJoystickEvent() {
        return joystickEvent;
    }
}