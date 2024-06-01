package it.unimi.di.ewlab.iss.common.model.actions;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Action implements Serializable {

    public enum ActionType {
        BUTTON (ButtonAction.class),
        VOCAL (VocalAction.class),
        SCREEN_GESTURE (ScreenGestureAction.class),
        FACIAL_EXPRESSION (FacialExpressionAction.class);

        public final Class<? extends Action> actionClass;

        ActionType(Class<? extends Action> actionClass) {
            this.actionClass = actionClass;
        }
    }

    private final String actionType;
    private String name;
    private final int actionId;
    private boolean is2d = false;


    protected Action(@NonNull ActionType actionType) {
        this.actionType = actionType.name();
        this.name = "";
        this.actionId = 0;
    }

    protected Action(int actionId, String name, ActionType actionType) {
        this.actionId = actionId;
        this.name = name.trim();
        this.actionType = actionType.name();
    }

    protected Action(String name, String ActionType, boolean is2d) {
        this.name = name;
        this.actionType = ActionType;
        this.is2d = is2d;
        this.actionId = 0;
    }

    public void setIs2d(boolean is2d) {
        this.is2d = is2d;
    }

    public boolean is2d() {
        return is2d;
    }

    public ActionType getActionType() {
        return ActionType.valueOf(actionType);
    }

    public int getActionId() {
        return actionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return actionType + " " + name + " (" + actionId + ")";
    }

    public boolean equals(Object other) {
        if(!(other instanceof Action))
            return false;

        Action otherAction = (Action) other;
        return this.getName().equals(otherAction.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public Action lighten() {
        return new Action(name, actionType, is2d);
    }

    public static Action FACE_MOVEMENT_ACTION = new Action("Face Movement", "FACIAL_EXPRESSION", true);
}
