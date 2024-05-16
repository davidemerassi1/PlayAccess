package it.unimi.di.ewlab.iss.common.configuratorsmodels

data class ActionListModel(
    val actionList: ArrayList<Action>
): BaseModel(){
    data class Action(
        val id: Int,
        val name: String,
        val notes: String?,
        val max_duration: Double?,
        val min_duration: Double?,
        val video: Any?
    ): BaseModel()
}