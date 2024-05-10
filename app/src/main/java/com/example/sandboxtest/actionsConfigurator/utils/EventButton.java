package com.example.sandboxtest.actionsConfigurator.utils;

import android.view.View;

import com.example.sandboxtest.database.Event;

public interface EventButton {
    public Integer getAction();
    public void setAction(int action);
    public void setOnClickListener(View.OnClickListener listener);
    public Event getEvent();
    public void setEvent(Event event);
}
