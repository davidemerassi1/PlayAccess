package it.unimi.di.ewlab.iss.common.configuratorsmodels

import it.unimi.di.ewlab.iss.common.model.Configuration

data class TrySingleConfigurationDataModel(
        val configurationList: ArrayList<SingleConfiguration>
): BaseModel(){
    data class SingleConfiguration(
            val configurationId: String,
            val configuration: Configuration
    ): BaseModel()
}