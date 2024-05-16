package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification

import android.util.Log
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

open class Prototypical : Classifier {

    companion object {
        private const val TAG = "Prototypical"
        const val DEFAULT_RADIUS = 0.80F
        private const val CERTAIN_RADIUS = 0.32F
    }

    private val trainSet = hashSetOf<Classifier.ClassifierInstance>()
    private val selectedFeaturesIndexes = hashSetOf<Int>()

    private var radius = DEFAULT_RADIUS
    private var selectedFeaturesCnt = 396

    override fun classify(features: List<Float>): Classifier.ClassifierResult {
        if (trainSet.isEmpty())
            return Classifier.NO_CLASSIFICATION

        var minTrain: Classifier.ClassifierInstance? = null
        var minTrainDistance: Double? = null

        for (train in trainSet) {
            val distance = distance(train, features)
            //Log.d(TAG, "Train \"${train.label}\" distance: $distance")
            if (minTrainDistance == null || distance < minTrainDistance) {
                minTrainDistance = distance
                minTrain = train
            }
        }

        return if (minTrainDistance!! < radius)
            Classifier.ClassifierResult(minTrain!!.label, minTrainDistance < CERTAIN_RADIUS)
        else Classifier.NO_CLASSIFICATION
    }

    override fun train(actions: List<FacialExpressionAction>): Boolean {
        Log.d(TAG, "train: set size ${actions.size}")

        trainSet.clear()
        selectedFeaturesIndexes.clear()

        if (actions.isEmpty())
            return false

        val features = actions[0].frames[0].features.indices.toMutableList()

        if (actions.size > 1) {
            val scores = getScores(actions)
            features.sortByDescending { f -> scores[f] }
        }

        val droppedFeatures = getDroppedFeatures(actions, features)

        features.removeAll(droppedFeatures)
        selectedFeaturesIndexes.addAll(
            features.subList(0, min(selectedFeaturesCnt, features.size))
        )

        Log.d(TAG,"Selected features: ${selectedFeaturesIndexes.size}")

        for (action in actions) {
            trainSet.add(
                Classifier.ClassifierInstance(action.means, action.actionId)
            )
        }

        return trainSet.size > 0
    }

    private fun getDroppedFeatures(actions: List<FacialExpressionAction>, featuresIndexes: List<Int>): Set<Int> {
        val droppedFeatures = mutableSetOf<Int>()

        val correlation = PearsonsCorrelation()

        val values = mutableMapOf<Int, DoubleArray>()

        for (f in featuresIndexes) {
            val featureValues = DoubleArray(actions.size * FacialExpressionAction.FRAMES_X_EXPRESSION)

            var k = 0
            for (action in actions) {
                for (frame in action)
                    featureValues[k++] = frame.features[f].toDouble()
            }

            values[f] = featureValues
        }

        for (i in featuresIndexes.indices) {
            val f1 = featuresIndexes[i]

            if (droppedFeatures.contains(f1))
                continue

            for (j in i+1 until featuresIndexes.size) {
                val f2 = featuresIndexes[j]
                if (
                    !droppedFeatures.contains(f2) &&
                    abs(correlation.correlation(values[f1], values[f2])) >= 0.95F
                )
                    droppedFeatures.add(f2)
            }
        }

        return droppedFeatures
    }

    private fun getScores(actions: List<FacialExpressionAction>): Map<Int, Double> {
        val scores = mutableMapOf<Int, Double>()

        val extractor = FisherScore()
        val featuresIndexes = actions[0].frames[0].features.indices

        for (feature in featuresIndexes) {
            for (action in actions) {
                for (frame in action)
                    extractor.add(frame.features[feature].toDouble(), action.name)
            }
            scores[feature] = extractor.score()
            extractor.clear()
        }

        return scores
    }

    private fun distance(train: Classifier.ClassifierInstance, features: List<Float>): Double {
        var dist = 0.0
        for (feature in selectedFeaturesIndexes) {
            val diff = train.features[feature] - features[feature]
            dist += diff * diff
        }
        return sqrt(dist)
    }

    // Definisce il raggio massimo di distanza tra il punto da classificare e i dati di training
    override fun setPrecision(precision: Float) {
        if (precision <= 0)
            throw IllegalArgumentException("precision can't be negative")
        radius = precision
    }

    override fun getPrecision(): Float {
        return radius
    }

    override fun resetPrecision() {
        radius = DEFAULT_RADIUS
    }
}
