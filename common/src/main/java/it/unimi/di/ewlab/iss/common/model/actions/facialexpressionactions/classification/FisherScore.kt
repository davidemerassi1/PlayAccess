package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification

import kotlin.math.pow

class FisherScore {
    private val data = mutableMapOf<String, MutableList<Double>>()
    private var grandMean = 0.0

    fun add(value: Double, className: String) {
        if (!data.containsKey(className)) {
            data[className] = mutableListOf()
        }
        data[className]?.add(value)
        grandMean += value
    }

    fun score(): Double {
        val numClasses = data.size

        require(numClasses > 1) { "requires two or more groups" }

        var betweenClassVariance = 0.0
        var withinClassVariance = 0.0
        val totalNumValues = data.values.flatten().size

        grandMean /= totalNumValues

        for ((_, values) in data) {
            val classMean = values.average()
            betweenClassVariance += values.size * (classMean - grandMean).pow(2)
            withinClassVariance += values.sumOf { (it - classMean).pow(2) }
        }

        betweenClassVariance /= (numClasses - 1)
        withinClassVariance /= (totalNumValues - numClasses)

        return betweenClassVariance / withinClassVariance
    }

    fun clear() {
        data.clear()
        grandMean = 0.0
    }
}
