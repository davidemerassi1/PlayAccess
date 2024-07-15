package it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.slider.Slider
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.ActivityTryFacialExpressionsBinding
import it.unimi.di.ewlab.iss.common.configuratorsmodels.BaseDialog
import it.unimi.di.ewlab.iss.common.model.Configuration
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Classifier
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Prototypical
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.CameraFrameListener
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.ClassificationFrameHandler
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.ClassificationListener
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.WrongPositioningListener
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler
import java.util.concurrent.ExecutionException
import kotlin.math.round
import kotlin.properties.Delegates

@ExperimentalGetImage
class TryFacialExpressionsActivity :
    AppCompatActivity(),
    CameraFrameListener,
    WrongPositioningListener,
    ClassificationListener {

    companion object {
        private const val TAG = "TryFacialExpressionsActivity"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 10
        private const val FIRST_TIME_KEY = "TryFacialExpressionsActivity_FIRST_TIME_KEY"

        private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        const val frameWidth = 480
        const val frameHeight = 640
    }

    private val binding: ActivityTryFacialExpressionsBinding by lazy {
        ActivityTryFacialExpressionsBinding.inflate(layoutInflater)
    }

    private lateinit var actions: MutableList<FacialExpressionAction>

    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var frameHandler: ClassificationFrameHandler
    private lateinit var frameHandlerThread: Thread

    private var selectedPrecision by Delegates.notNull<Float>()

    private lateinit var dialog: Dialog
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        actions = MainModel.getInstance().facialExpressionActions
        actions.add(MainModel.getInstance().neutralFacialExpressionAction)

        frameHandler = ClassificationFrameHandler(
            this,
            this,
            this
        )

        val infoDialog = BaseDialog(this).apply {
            titleText = R.string.tryFacialExpressions_title
            setSubTitleString("Da qui puoi provare le espressioni facciali e impostare la precisione del riconoscimento. ")
            textPrimaryButton = android.R.string.ok
            setColorsByActionType(Action.ActionType.FACIAL_EXPRESSION)
        }

        infoDialog.listener = object : BaseDialog.BaseDialogListener {
            override fun onPrimaryButtonClicked() {
                if (PermissionsHandler.checkCameraPermission(this@TryFacialExpressionsActivity))
                    dialog.dismiss()
                else askCameraPermission()
            }

            override fun onSecondaryButtonClicked() {}
            override fun onCheckClicked(checked: Boolean) {}
        }

        dialog = infoDialog
        selectedPrecision = MainModel.getInstance().precision
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        setUi()

        val persistenceManager = PersistenceManager(this)
        if (persistenceManager.getValue(FIRST_TIME_KEY, true) as Boolean) {
            dialog.show()
            persistenceManager.setValue(FIRST_TIME_KEY, false)
        } else if (PermissionsHandler.checkCameraPermission(this)) {
            askCameraPermission()
        }

        frameHandlerThread = Thread(frameHandler)
        frameHandlerThread.start()

        startCameraCapture()
    }

    private fun askCameraPermission() {
        PermissionsHandler.askCameraPermission(
            this,
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun startCameraCapture() {
        Log.d(TAG, "startCameraCapture")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
            } catch (e: ExecutionException) {
                Log.e(TAG, "Error getting camera provider: " + e.localizedMessage)
            }

            if (cameraProvider == null) {
                showCameraInitializationErrorMessage()
                finishAndRemoveTask()
                return@addListener
            }

            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(frameWidth, frameHeight))
                .build()

            analysisUseCase.setAnalyzer(
                ContextCompat.getMainExecutor(this)
            ) {
                try {
                    processFrame(it)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to process frame: ${e.localizedMessage}")
                    it.close()
                }
            }

            if (!frameHandler.init(this)) {
                showCameraInitializationErrorMessage()
                finishAndRemoveTask()
            }
            frameHandler.trainClassifier(actions)
            frameHandler.setClassifierPrecision(selectedPrecision)

            //cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(this, cameraSelector, analysisUseCase)
            binding.loadingCameraProgressBar.visibility = View.GONE
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processFrame(imageProxy: ImageProxy) {
        if (imageProxy.image == null)
            imageProxy.close()
        else
            frameHandler.onImageProxy(imageProxy)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        frameHandler.stop()
        frameHandlerThread.interrupt()
    }

    override fun onDestroy() {
        super.onDestroy()
        window.statusBarColor = ContextCompat.getColor(this, R.color.emerald)
    }

    private fun setUi() {
        binding.backBtn.setOnClickListener {
            handler.postDelayed({ finish() }, 500)
        }

        binding.infoBtn.setOnClickListener {
            dialog.show()
        }

        binding.acquiredExpression.msg.textSize = 28F

        binding.precisionSlider.value = selectedPrecision / Prototypical.DEFAULT_RADIUS
        binding.precisionSlider.setLabelFormatter { value -> "${round(value * 10) / 10}x" }

        binding.confirmPrecision.root.setOnClickListener {
            onPause()
            MainModel.getInstance().precision = selectedPrecision
            handler.postDelayed({ finish() }, 500)
        }

        binding.cancelPrecision.root.setOnClickListener {
            onPause()
            handler.postDelayed({ finish() }, 500)
        }

        binding.precisionSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                selectedPrecision = slider.value * Prototypical.DEFAULT_RADIUS
                frameHandler.setClassifierPrecision(selectedPrecision)
            }
        })
    }

    override fun onCameraFrameAnalyzed(bitmap: Bitmap) {
        binding.root.post {
            binding.cameraCapture.setImageBitmap(bitmap)
        }
    }

    override fun onWrongPositioning(err: WrongPositioningListener.PositioningError) {
        binding.root.post {
            when (err) {
                WrongPositioningListener.PositioningError.NO_FACE_DETECTED -> {
                    binding.missingFaceErrorBox.root.visibility = View.VISIBLE
                    binding.missingLandmarkErrorBox.root.visibility = View.GONE
                }
                WrongPositioningListener.PositioningError.MISSING_FACE_LANDMARK -> {
                    binding.missingFaceErrorBox.root.visibility = View.GONE
                    binding.missingLandmarkErrorBox.root.visibility = View.VISIBLE
                }
                else -> {}
            }
            binding.acquiredExpression.text = ""
        }
    }

    override fun onPositioningRestored() {
        binding.root.post {
            binding.missingFaceErrorBox.root.visibility = View.GONE
            binding.missingLandmarkErrorBox.root.visibility = View.GONE
        }
    }

    private fun showCameraInitializationErrorMessage() {
        Toast.makeText(
            this,
            getString(R.string.tryFacialExpressions_camera_initialization_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onClassification(classification: Classifier.ClassifierResult) {
        if (classification.label >= 0)
            binding.acquiredExpression.text =
                MainModel.getInstance().getActionById(classification.label)!!.name
        else
            binding.acquiredExpression.text =
                getString(R.string.tryFacialExpressions_unrecognized_expression)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode != CAMERA_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(
                this,
                R.string.tryFacialExpressions_no_permission,
                Toast.LENGTH_SHORT
            ).show()
            finishAndRemoveTask()
        }

        dialog.dismiss()
        startCameraCapture()
    }

    override fun onBackPressed() {
        handler.postDelayed({ super.onBackPressed() }, 500)
    }
}