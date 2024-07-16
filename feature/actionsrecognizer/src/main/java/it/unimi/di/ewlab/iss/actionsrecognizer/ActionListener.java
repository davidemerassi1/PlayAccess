package it.unimi.di.ewlab.iss.actionsrecognizer;

import org.checkerframework.checker.nullness.qual.NonNull;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public interface ActionListener {
    void onActionStarts(@NonNull Action action);

    void onActionEnds(@NonNull Action action);

    void on2dMovement(Action action, float x, float y);
}
