package it.unimi.di.ewlab.iss.common.model.actionsmodels;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.actions.ScreenGestureAction;

public class ScreenGestureActionsModel extends ActionsModel {

    private final List<ScreenGestureAction> screenGestureActions;

    public ScreenGestureActionsModel() {
        this.modelName = "ScreenGestureActionModel";
        this.screenGestureActions = new ArrayList<>();
    }

    public List<ScreenGestureAction> getScreenGestureActions() {
        return Collections.unmodifiableList(screenGestureActions);
    }

    public void setScreenGestureActions(List<ScreenGestureAction> screenGestureActionList) {
        this.screenGestureActions.clear();
        this.screenGestureActions.addAll(screenGestureActionList);
    }

    public void addScreenGestureAction(@NonNull ScreenGestureAction screenGestureAction) {
        if (getScreenGestureActionByLayoutName_BtnId(screenGestureAction.getS_g_a_BtnId()) == null)
            this.screenGestureActions.add(screenGestureAction);
    }

    public void deleteScreenGestureAction(@NonNull ScreenGestureAction screenGestureAction) {
        this.screenGestureActions.remove(screenGestureAction);
    }

    public ScreenGestureAction getScreenGestureActionByLayoutName_BtnId(ScreenGestureAction.GestureId btdId) {
        for (ScreenGestureAction screenGestureAction : screenGestureActions) {
            if (screenGestureAction.getS_g_a_BtnId().equals(btdId)) return screenGestureAction;
        }
        return null;
    }
}
