package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler

import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Classifier

interface ClassificationListener {
    fun onClassification(classification: Classifier.ClassifierResult)
}