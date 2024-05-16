package it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.itemaction.ActionItem
import it.unimi.di.ewlab.iss.common.configuratorsmodels.Event
import it.unimi.di.ewlab.iss.common.model.actions.Action

class ActionsListViewModel: ViewModel() {

    private val _actionList = MutableLiveData<List<ActionItem>>()
    val actionList: LiveData<List<ActionItem>> = _actionList

    private val _eventhandler = MutableLiveData<Event<ActionManagerEvent>>()
    val eventhandler: LiveData<Event<ActionManagerEvent>> = _eventhandler

    fun initActionRecycler(actions: List<Action>) {
        val items = ArrayList<ActionItem>()
        for (i in actions.indices) {
            val item = ActionItem(
                id = actions[i].actionId,
                name = actions[i].name,
                actionType = actions[i].actionType,
                onClick = {
                    _eventhandler.value =
                        Event(ActionManagerEvent.ActionManagerEventClick(it))
                },
                onDelete = {
                    items.remove(it)
                    _eventhandler.value =
                        Event(ActionManagerEvent.ActionManagerDelete(it))
                }
            )
            items.add(item)
        }
        items.sortBy { it.name }
        _actionList.value = items
    }
}

sealed class ActionManagerEvent{
    data class ActionManagerEventClick(val item: ActionItem): ActionManagerEvent()
    data class ActionManagerDelete(val item: ActionItem): ActionManagerEvent()
    object ActionManagerRetrievedActionList: ActionManagerEvent()
    object ActionManagerClickWarning: ActionManagerEvent()
}