package it.unimi.di.ewlab.iss.common.model.actionsmodels;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;

public class ButtonActionsModel extends ActionsModel {

    private final List<ButtonAction> buttonActions;

    public ButtonActionsModel() {
        this.modelName = "ButtonActionModel";
        this.buttonActions = new ArrayList<>();
    }


    public List<ButtonAction> getButtonActions() {
        return Collections.unmodifiableList(buttonActions);
    }

    public void setButtonActions(List<ButtonAction> buttonActions) {
        this.buttonActions.clear();
        this.buttonActions.addAll(buttonActions);
    }

    public void addButtonAction(@NonNull ButtonAction buttonAction) {
        if (getButtonActionByIds(buttonAction.getSourceId(), buttonAction.getKeyId()) == null)
            this.buttonActions.add(buttonAction);
    }

    public void deleteButtonAction(@NonNull ButtonAction buttonAction) {
        this.buttonActions.remove(buttonAction);
    }

    public ButtonAction getButtonActionByName(String name) {
        for (ButtonAction buttonAction : buttonActions) {
            if (buttonAction.getName().equals(name)) return buttonAction;
        }
        return null;
    }

    public ButtonAction getButtonActionByIds(String sourceId, String keyID) {
        for (ButtonAction buttonAction : buttonActions) {
            if (buttonAction.getSourceId().equals(sourceId) && buttonAction.getKeyId().equals(keyID)) return buttonAction;
        }
        return null;
    }
}
