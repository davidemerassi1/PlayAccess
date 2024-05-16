package com.example.sandboxtest.actionsConfigurator.utils;

import android.view.View;

import com.example.sandboxtest.database.Event;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public interface EventButton {
    public Action getAction();
    public void setAction(Action action);
    public void setOnClickListener(View.OnClickListener listener);
    public Event getEvent();
    public void setEvent(Event event);
}
