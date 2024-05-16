package it.unimi.di.ewlab.iss.common.configuratorsmodels

import java.util.*
import kotlin.collections.ArrayList

data class ActionsPosesLinkListDataModel(
        val actionList: ArrayList<ActionPosesLink>
): BaseModel(){
    data class ActionPosesLink(
            val idAction: Int,
            val poses: ArrayList<Pose>
    ): BaseModel(){
        data class Pose(
                val label: String,
                val bitmap: String,
                val features: SortedMap<String, Float>
        ): BaseModel()
    }
}