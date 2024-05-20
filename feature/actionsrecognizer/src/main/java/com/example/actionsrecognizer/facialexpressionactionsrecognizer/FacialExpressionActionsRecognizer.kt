package com.example.actionsrecognizer.facialexpressionactionsrecognizer

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import it.unimi.di.ewlab.iss.common.model.Configuration
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.ClassificationFrameHandler
import it.unimi.di.ewlab.iss.common.model.actionsmodels.FacialExpressionActionsModel
import java.util.concurrent.ExecutionException

@ExperimentalGetImage
class FacialExpressionActionsRecognizer private constructor(
    actions: List<Action>?,
    actionListeners: List<ActionListener?>?,
) :
    ActionsRecognizer(actions, actionListeners),
    ActionListener {

    private lateinit var frameHandler: ClassificationFrameHandler
    private lateinit var frameHandlerThread: Thread

    private var isInitialized = false

    fun init(
        context: Context,
        lifecycleOwner: LifecycleOwner
    ) {

        val feModel = initFeModel()
        initFrameHandler(
            context,
            feModel
        )
        startCameraCapture(context, lifecycleOwner)


        isInitialized = true

        frameHandlerThread = Thread(this.frameHandler)
        frameHandlerThread.start()

    }

    private fun initFeModel(): FacialExpressionActionsModel {
        val facialexpressionactions = ArrayList<FacialExpressionAction>()
        for (action in actions) {
            if (action is FacialExpressionAction)
                facialexpressionactions.add(action)
        }
        val feModel = FacialExpressionActionsModel()
        feModel.setFacialExpressionActions(facialexpressionactions)
        feModel.addFacialExpressionAction(MainModel.getInstance().neutralFacialExpressionAction!!)
        return feModel
    }

    private fun initFrameHandler(
        context: Context,
        feModel: FacialExpressionActionsModel,
    ) {
        val fsm = FiniteStateMachine(feModel, this)
        val filter = ClassificationsFilter(Configuration.Settings.FacialExpressionPrecision.MEDIUM, fsm)

        frameHandler = ClassificationFrameHandler(null, filter, filter)

        frameHandler.drawLandmarks = false
        frameHandler.init(context)

        frameHandler.trainClassifier(feModel.actions)

        frameHandler.setClassifierPrecision(0.80F)
    }


    private fun startCameraCapture(context: Context, lifecycleOwner: LifecycleOwner) {
        Log.d(TAG, "startCameraCapture")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            var cameraProvider: ProcessCameraProvider? = null
            try {
                cameraProvider = cameraProviderFuture.get()
            } catch (e: ExecutionException) {
                Log.e(TAG, "Error getting camera provider: ${e.localizedMessage}")
            } catch (e: InterruptedException) {
                Log.e(TAG, "Error getting camera provider: ${e.localizedMessage}")
            }
            if (cameraProvider == null)
                return@addListener

            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(cameraWidth, cameraHeight))
                .build()

            analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(context)) {
                try {
                    processFrame(it)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to process frame: ${e.localizedMessage}")
                    it.close()
                }
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, analysisUseCase)

        }, ContextCompat.getMainExecutor(context))
    }

    private fun processFrame(imageProxy: ImageProxy) {
        //Log.d(TAG, "processFrame")
        if (imageProxy.image == null)
            imageProxy.close()
        else frameHandler.onImageProxy(imageProxy)
    }

    override fun onActionStarts(action: Action) {
        startAction(action)
    }

    override fun onActionEnds(action: Action) {
        endAction(action)
    }

    companion object {
        private const val TAG = "FacialExpressionActionR"

        private var instance: FacialExpressionActionsRecognizer? = null

        private const val cameraWidth = 480
        private const val cameraHeight = 640
        private val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        fun getInstance(): FacialExpressionActionsRecognizer {
            if (instance == null) {
                Log.e(
                    TAG,
                    "instance is null; call getInstance(selectedConfiguration, actionListeners) first"
                )
                throw IllegalStateException("instance is null; call getInstance(selectedConfiguration, actionListeners) first")
            }

            return instance!!
        }

        @Synchronized
        fun getInstance(
            actions: List<Action>?,
            actionListeners: List<ActionListener?>?,
        ): FacialExpressionActionsRecognizer {
            instance = FacialExpressionActionsRecognizer(actions, actionListeners)
            return instance!!
        }
    }

    override fun startAnalysis() {
        TODO("Not yet implemented")
    }

    override fun stopAnalysis() {
        TODO("Not yet implemented")
    }
}
