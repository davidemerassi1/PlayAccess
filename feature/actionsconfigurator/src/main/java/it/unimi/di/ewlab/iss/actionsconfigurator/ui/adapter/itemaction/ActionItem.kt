package it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.itemaction

import it.unimi.di.ewlab.iss.common.model.actions.Action

data class ActionItem(
    val id: Int,
    val name: String,
    val actionType: Action.ActionType,
    var onClick: (ActionItem) -> Unit,
    var onDelete: (ActionItem) -> Unit,
)