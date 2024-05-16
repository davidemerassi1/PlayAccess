package it.unimi.di.ewlab.iss.common.configuratorsmodels

data class GamesListModel(
    val gamesList: ArrayList<Game>
): BaseModel(){
    data class Game(
        val gid: String, //bundleId
        val title: String,
        val icon: String?,
        val screen: String?
    ): BaseModel()
}