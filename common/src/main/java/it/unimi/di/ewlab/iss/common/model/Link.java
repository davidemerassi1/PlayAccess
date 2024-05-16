package it.unimi.di.ewlab.iss.common.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class Link implements Serializable {
    private Event eventObject = null;
    private int actionId = -1;
    private transient Action startAction = null;
    private int markerColor = 0;
    private int markerSize = 0;

    private Action dxAction = null;
    private Action sxAction = null;
    private int duration;


    public Link() {
    }

    public Link(Event event, int markerColor, int markerSize) {
        this.eventObject = event;
        this.markerColor = markerColor;
        this.markerSize = markerSize;
    }

    public Link(Event event, Action action, Action dxAction, Action sxAction, int markerColor, int markerSize) {
        this.eventObject = event;
        this.actionId = action != null ? action.getActionId() : -1;
        this.markerColor = markerColor;
        this.markerSize = markerSize;
        this.dxAction = dxAction;
        this.sxAction = sxAction;
    }

    public Event getEvent() {
        return eventObject;
    }

    public void setEvent(Event eventName) {
        this.eventObject = eventName;
    }

    public int getMarkerColor() {
        return markerColor;
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public int getDuration() {
        return duration;
    }

    public Action getAction() {
        if (actionId <= 0) return null;
        if (startAction == null)
            startAction = MainModel.getInstance().getActionById(actionId);
        return startAction;
    }

    public void setAction(Action action) {
        this.actionId = action != null ? action.getActionId() : -1;
        this.startAction = action;
    }

    public int getActionId() {
        return actionId;
    }

    public void setMarkerColor(int markerColor) {
        this.markerColor = markerColor;
    }

    public void setMarkerSize(int markerSize) {
        this.markerSize = markerSize;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * This method checks if the link has an action and, if the link is an On / Off link,
     * if it also has an action Stop
     *
     * @return true if the link is fully defined
     */
    public boolean isFullyDefined() {
        return eventObject != null && eventObject.getType() != null && actionId > 0;
    }

    public void setDxAction(Action dxAction) {
        this.dxAction = dxAction;
    }

    public void setSxAction(Action sxAction) {
        this.sxAction = sxAction;
    }

    public Action getDxAction() {
        return dxAction;
    }

    public Action getSxAction() {
        return sxAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (o instanceof Link)) return false;

        Link link = (Link) o;

        if (actionId != link.actionId) return false;
        if (markerColor != link.markerColor) return false;
        if (markerSize != link.markerSize) return false;
        if (duration != link.duration) return false;
        return Objects.equals(eventObject, link.eventObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventObject, actionId, markerColor, markerSize, duration);
    }

    @Override
    @NonNull
    public String toString() {
        return "Link {" +
                "event=" + eventObject +
                ", actionId=" + actionId +
                ", markerColor=" + markerColor +
                ", markerSize=" + markerSize +
                ", duration=" + duration +
                '}';
    }

    public Link clone() {
        Link cloned = new Link();
        cloned.eventObject = eventObject;
        cloned.actionId = actionId;
        cloned.startAction = startAction;
        cloned.markerColor = markerColor;
        cloned.markerSize = markerSize;
        cloned.duration = duration;
        cloned.dxAction = dxAction;
        cloned.sxAction = sxAction;
        return cloned;
    }
}