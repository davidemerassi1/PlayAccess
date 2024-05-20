package actionsConfigurator.utils;

import android.view.View;

import it.unimi.di.ewlab.iss.common.database.Event;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public interface EventButton {
    public Action getAction();
    public void setAction(Action action);
    public void setOnClickListener(View.OnClickListener listener);
    public Event getEvent();
    public void setEvent(Event event);
}
