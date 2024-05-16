package it.unimi.di.ewlab.iss.common.configuratorsmodels

import it.unimi.di.ewlab.iss.common.model.Configuration

data class ConfigurationsForAccessibilityService(
        val configurationList: ArrayList<Configuration>
): BaseModel()