package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.featuresextractors

import android.util.Log
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmark
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmarkTranslator
import it.unimi.di.ewlab.iss.common.utils.Utils.angleBetweenSegmentAndY

object AngleFacialFeaturesExtractor: FacialFeaturesExtractor {

    private const val TAG = "AngleFacialFeaturesExtractor"

    override fun extractFeatures(landmarks: List<FaceLandmark>): List<Float> {
        if (landmarks.size != FaceLandmarkTranslator.LANDMARKS_CNT) {
            Log.e(TAG, "extractFeatures: missing landmarks ${landmarks.size}/${FaceLandmarkTranslator.LANDMARKS_CNT}")
            return listOf()
        }

        return listOf(
            angleBetweenSegmentAndY(landmarks[2].framePosition, landmarks[168].framePosition)
        )
    }
}