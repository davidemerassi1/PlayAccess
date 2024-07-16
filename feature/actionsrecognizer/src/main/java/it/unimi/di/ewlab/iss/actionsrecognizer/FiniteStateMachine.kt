package it.unimi.di.ewlab.iss.actionsrecognizer

import android.util.Log
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actionsmodels.FacialExpressionActionsModel

class FiniteStateMachine(
    feModel: FacialExpressionActionsModel,
    private val actionsListener: ActionListener
) : FilterListener {

    private val actions = hashMapOf<Int?, Action>()
    private var currentAction = -1

    init {
        for (action in feModel.actions)
            actions[action.actionId] = action
    }

    fun updateActions(feModel: FacialExpressionActionsModel) {
        actions.clear()
        for (action in feModel.actions)
            actions[action.actionId] = action
    }

    override fun onClassification(classification: Int) {
        Log.d("FiniteStateMachine", "Classification: $classification")
        if (classification != currentAction) {
            if (currentAction >= 0 && currentAction != MainModel.NEUTRAL_FACIAL_EXPRESSION_ACTION_ID)
                actionsListener.onActionEnds(actions[currentAction]!!)

            currentAction = classification

            if (currentAction >= 0 && currentAction != MainModel.NEUTRAL_FACIAL_EXPRESSION_ACTION_ID)
                actionsListener.onActionStarts(actions[currentAction]!!)
        }
    }
}
