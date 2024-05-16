package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification

import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction

interface Classifier {

    fun classify(features: List<Float>): ClassifierResult

    // Training basato sulle espressioni passate
    fun train(actions: List<FacialExpressionAction>): Boolean

    // Training basato su tutte le espressioni registrate
    fun train(): Boolean {
        return train(MainModel.getInstance().facialExpressionActions)
    }

    // Definisce la precisione dell'algoritmo di classificazione
    // Il significato di "precisione" dipende dall'algoritmo specifico
    fun setPrecision(precision: Float)

    fun getPrecision(): Float

    fun resetPrecision()

    data class ClassifierInstance(val features: List<Float>, val label: Int)
    data class ClassifierResult(val label: Int, val certain: Boolean)
    companion object {
        val NO_CLASSIFICATION =
            ClassifierResult(
                -1,
                false
            )
    }
}