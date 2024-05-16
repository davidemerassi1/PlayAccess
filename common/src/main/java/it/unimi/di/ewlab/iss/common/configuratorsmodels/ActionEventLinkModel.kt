package it.unimi.di.ewlab.iss.common.configuratorsmodels


data class ActionEventLinkModel(
    val action: Int,
    val event: String,
    val x: Double,
    val y: Double
): BaseModel()