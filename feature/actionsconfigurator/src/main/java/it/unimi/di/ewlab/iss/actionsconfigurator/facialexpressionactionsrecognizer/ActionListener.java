package it.unimi.di.ewlab.iss.actionsconfigurator.facialexpressionactionsrecognizer;

import org.checkerframework.checker.nullness.qual.NonNull;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public interface ActionListener {
    void onActionStarts(@NonNull Action action);

    void onActionEnds(@NonNull Action action);
}
