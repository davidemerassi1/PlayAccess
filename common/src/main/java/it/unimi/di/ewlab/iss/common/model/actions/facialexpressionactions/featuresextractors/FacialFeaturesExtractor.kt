package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.featuresextractors

import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmark

interface FacialFeaturesExtractor {
    fun extractFeatures(landmarks: List<FaceLandmark>): List<Float>
}