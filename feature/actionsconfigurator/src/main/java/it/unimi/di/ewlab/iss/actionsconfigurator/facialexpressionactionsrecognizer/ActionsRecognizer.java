package it.unimi.di.ewlab.iss.actionsconfigurator.facialexpressionactionsrecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.Configuration;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

public abstract class ActionsRecognizer {
    private final Configuration selectedConfiguration;
    private final List<ActionListener> actionListeners;

    protected ActionsRecognizer(Configuration selectedConfiguration, List<ActionListener> actionListeners) {
        this.selectedConfiguration = selectedConfiguration;
        this.actionListeners = new ArrayList<>(actionListeners);
    }

    protected void startAction(Action action){
        for(ActionListener listener: actionListeners){
            listener.onActionStarts(action);
        }
    }

    protected void endAction(Action action){
        for(ActionListener listener: actionListeners){
            listener.onActionEnds(action);
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

    public Configuration getSelectedConfiguration(){
        return selectedConfiguration;
    }
}
