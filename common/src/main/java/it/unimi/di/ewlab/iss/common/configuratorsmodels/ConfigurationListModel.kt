package it.unimi.di.ewlab.iss.common.configuratorsmodels

data class ConfigurationListModel(
    val configurationsList: ArrayList<Configuration>
): BaseModel(){
    data class Configuration(
        val id: Int,
        val name: String,
        val action_event_coords: ArrayList<ActionEventLinkModel>
    ): BaseModel()
}