package it.unimi.di.ewlab.iss.common.configuratorsmodels

import it.unimi.di.ewlab.iss.common.model.Configuration

data class TryActionConfigurationDataModel(
        val actionList: ArrayList<SingleActionConfiguration>
): BaseModel(){
    data class SingleActionConfiguration(
            val actionId: String,
            val configuration: Configuration
    ): BaseModel()
}