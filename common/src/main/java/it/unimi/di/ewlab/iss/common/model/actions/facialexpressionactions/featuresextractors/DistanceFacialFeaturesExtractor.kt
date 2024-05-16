package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.featuresextractors

import android.util.Log
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmark
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmarkTranslator
import it.unimi.di.ewlab.iss.common.utils.Utils.euclideanDistance

object DistanceFacialFeaturesExtractor: FacialFeaturesExtractor {
    private const val TAG = "AngleFacialFeaturesExtractor"

    private val pairs = mutableListOf<Pair<Int, Int>>()
    val isInitialized: Boolean
        get() = pairs.isNotEmpty()

    fun init(landmarksPairs: List<Pair<Int, Int>>): Boolean {
        pairs.clear()
        pairs.addAll(landmarksPairs)
        return isInitialized
    }

    override fun extractFeatures(landmarks: List<FaceLandmark>): List<Float> {
        if (landmarks.size != FaceLandmarkTranslator.LANDMARKS_CNT) {
            Log.e(TAG, "extractFeatures: missing landmarks ${landmarks.size}/${FaceLandmarkTranslator.LANDMARKS_CNT}")
            return listOf()
        }

        if (!isInitialized) {
            Log.e(TAG, "DistanceFacialFeaturesExtractor not initialized; call init(List<Int, Int>) first")
            return listOf()
        }

        val features = mutableListOf<Float>()

        for ((i, j) in pairs)
            features.add(euclideanDistance(landmarks[i].facePosition, landmarks[j].facePosition))

        return features
    }
}