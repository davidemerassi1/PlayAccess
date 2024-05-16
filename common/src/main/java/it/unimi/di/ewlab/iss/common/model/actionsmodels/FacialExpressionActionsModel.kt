package it.unimi.di.ewlab.iss.common.model.actionsmodels

import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction

class FacialExpressionActionsModel : ActionsModel() {

    companion object {
        private const val TAG = "FacialExpressionActionsModel"
    }

    private var _actions: MutableList<FacialExpressionAction> = mutableListOf()
    val actions: List<FacialExpressionAction>
        get() = _actions

    init {
        modelName = "FacialExpressionActionsModel"
    }

    fun setFacialExpressionActions(actions: List<FacialExpressionAction>) {
        _actions.clear()
        _actions.addAll(actions)
    }

    fun addFacialExpressionAction(action: FacialExpressionAction) {
        if (getFacialExpressionById(action.actionId) == null)
            _actions.add(action)
    }

    fun removeFacialExpressionAction(action: FacialExpressionAction) {
        _actions.remove(action)
    }

    fun getFacialExpressionById(actionId: Int): FacialExpressionAction? {
        for (action in actions) {
            if (action.actionId == actionId)
                return action
        }
        return null
    }
}