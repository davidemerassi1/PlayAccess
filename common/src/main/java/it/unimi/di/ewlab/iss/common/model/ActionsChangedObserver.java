package it.unimi.di.ewlab.iss.common.model;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public interface ActionsChangedObserver {
    void onActionsChanged(Action removedAction);
}
