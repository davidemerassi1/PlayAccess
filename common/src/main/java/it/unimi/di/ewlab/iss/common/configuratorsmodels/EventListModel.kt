package it.unimi.di.ewlab.iss.common.configuratorsmodels

import it.unimi.di.ewlab.iss.common.model.EventType

data class EventListModel(
    val eventList: ArrayList<Event>
): BaseModel(){
    data class Event(
        val eventType: EventType
    ): BaseModel()
}