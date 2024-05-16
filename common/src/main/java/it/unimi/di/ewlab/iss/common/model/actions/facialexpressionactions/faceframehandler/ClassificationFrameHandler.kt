package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Prototypical
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmark
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.featuresextractors.AngleFacialFeaturesExtractor
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.featuresextractors.DistanceFacialFeaturesExtractor

@ExperimentalGetImage class ClassificationFrameHandler(
    cameraFrameListener: CameraFrameListener?,
    wrongPositioningListener: WrongPositioningListener?,
    private val classificationListener: ClassificationListener?
) : FaceFrameHandler(cameraFrameListener, wrongPositioningListener) {

    companion object {
        private const val TAG = "ClassificationFrameHandler"
    }

    override val operatorThreadName: String
        get() = "ClassificationFrameHandler"

    private val featuresExtractors = listOf(
        DistanceFacialFeaturesExtractor,
        AngleFacialFeaturesExtractor
    )

    private val classifier = Prototypical()

    override fun init(context: Context): Boolean {
        Log.d(TAG, "init")

        var isExtractorInitialized = true
        if (!DistanceFacialFeaturesExtractor.isInitialized)
            isExtractorInitialized = DistanceFacialFeaturesExtractor.init(
                MainModel.getInstance().getFacialFeaturesDistances(context)
            )

        if (isExtractorInitialized) {
            synchronized(this) {
                canProcessLandmarks = true
            }
        }

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

        classificationListener?.onClassification(
            classifier.classify(features)
        )
    }

    fun trainClassifier(actions: List<FacialExpressionAction>) {
        classifier.train(actions)
    }

    fun setClassifierPrecision(precision: Float) {
        classifier.setPrecision(precision)
    }
}