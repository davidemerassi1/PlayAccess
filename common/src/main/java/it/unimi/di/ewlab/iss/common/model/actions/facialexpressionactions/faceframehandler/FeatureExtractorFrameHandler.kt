package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmark
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.featuresextractors.AngleFacialFeaturesExtractor
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.featuresextractors.DistanceFacialFeaturesExtractor

@ExperimentalGetImage class FeatureExtractorFrameHandler(
    cameraFrameListener: CameraFrameListener?,
    wrongPositioningListener: WrongPositioningListener?,
    private val expressionFrameListener: ExpressionFrameListener?
) : FaceFrameHandler(cameraFrameListener, wrongPositioningListener) {

    companion object {
        private const val TAG = "FeatureExtractorFrameHandler"
    }

    override val operatorThreadName: String
        get() = "FeatureExtractorFrameHandler"

    private val featuresExtractors = listOf(
        DistanceFacialFeaturesExtractor,
        AngleFacialFeaturesExtractor
    )

    private var featureExtractedCnt = 0

    override fun init(context: Context): Boolean {
        Log.d(TAG, "init")

        var isExtractorInitialized = true
        if (!DistanceFacialFeaturesExtractor.isInitialized)
            isExtractorInitialized = DistanceFacialFeaturesExtractor.init(
                MainModel.getInstance().getFacialFeaturesDistances(context)
            )

        return super.init(context) && isExtractorInitialized
    }

    override fun processLandmarks(
        landmarks: List<FaceLandmark>,
        landmarksBitmap: Bitmap
    ) {
        val features = mutableListOf<Float>()
        for (featureExtractor in featuresExtractors)
            features.addAll(
                featureExtractor.extractFeatures(landmarks)
            )

        expressionFrameListener?.onExpressionFrame(
            Frame(
                landmarksBitmap,
                features
            )
        )

        featureExtractedCnt++
        if (featureExtractedCnt == FacialExpressionAction.FRAMES_X_EXPRESSION)
            synchronized(this) {
                canProcessLandmarks = false
            }
    }

    fun startFeatureExtraction() {
        if (canProcessLandmarks) return
        synchronized(this) {
            featureExtractedCnt = 0
            canProcessLandmarks = true
        }
    }
}