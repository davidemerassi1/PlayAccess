package com.example.actionsrecognizer.facialexpressionactionsrecognizer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public abstract class ActionsRecognizer {
    protected List<Action> actions;
    private final List<ActionListener> actionListeners;

    protected ActionsRecognizer(List<Action> actions, List<ActionListener> actionListeners) {
        this.actions = actions;
        this.actionListeners = new ArrayList<>(actionListeners);
    }

    protected void startAction(Action action){
        Log.d("ActionsRecognizer", "startAction: " + action);
        for(ActionListener listener: actionListeners){
            listener.onActionStarts(action);
        }
    }

    protected void endAction(Action action){
        for(ActionListener listener: actionListeners){
            listener.onActionEnds(action);
        }
    }

    protected void send2dMovement(Action action, float x, float y){
        for(ActionListener listener: actionListeners){
            listener.on2dMovement(action, x, y);
        }
    }
    
    protected abstract void startAnalysis();

    protected abstract void stopAnalysis();

    public void setActionListeners(List<ActionListener> actionListeners) {
        this.actionListeners.clear();
        this.actionListeners.addAll(actionListeners);
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
    }

    public List<ActionListener> getActionListeners() {
        return Collections.unmodifiableList(actionListeners);
    }
}
