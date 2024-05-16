package it.unimi.di.ewlab.iss.common.configuratorsmodels

import it.unimi.di.ewlab.iss.common.model.Game

data class GamesForAccessibilityServiceModel(
    val gameList: ArrayList<Game>
): BaseModel()