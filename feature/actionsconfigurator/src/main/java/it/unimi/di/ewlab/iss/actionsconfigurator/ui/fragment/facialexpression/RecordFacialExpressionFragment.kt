package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.facialexpression

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentRecordFacialExpressionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.FacialExpressionActivity
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.InfoFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.info.InfoFacialExpressionActionFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.FacialExpressionViewModel
import it.unimi.di.ewlab.iss.common.configuratorsmodels.BaseDialog
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.ExpressionFrameListener
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.FeatureExtractorFrameHandler
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.WrongPositioningListener
import it.unimi.di.ewlab.iss.common.storage.PersistenceManager
import it.unimi.di.ewlab.iss.common.utils.PermissionsHandler
import java.util.concurrent.ExecutionException
import kotlin.properties.Delegates

@ExperimentalGetImage
class RecordFacialExpressionFragment : Fragment(),
    it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.CameraFrameListener,
    WrongPositioningListener, ExpressionFrameListener,
    InfoFragment {

    companion object {
        private const val TAG = "RecordFacialExpressionFragment"
        private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        const val frameWidth = 480
        const val frameHeight = 640
        private const val RECORDING_FIRST_TIME_KEY =
            "RecordFacialExpressionFragment_RECORDING_FIRST_TIME_KEY"
    }

    private lateinit var binding: FragmentRecordFacialExpressionBinding
    private val viewModel: FacialExpressionViewModel by activityViewModels()
    private var actionId: Int? = null
    private val isReRecording: Boolean
        get() = actionId != null

    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var frameHandler: FeatureExtractorFrameHandler
    private lateinit var frameHandlerThread: Thread

    private var recordNeutralExpression by Delegates.notNull<Boolean>()
    private var infoDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionId = arguments?.getInt(FacialExpressionActivity.ARG_ID_ACTION)
        actionId?.let {
            val action = MainModel.getInstance().getActionById(it)
            if (action == null || action !is FacialExpressionAction) {
                Log.e(TAG, "No facial expression action found with actionId $it")
                throw IllegalStateException("No facial expression action found with actionId $it")
            }
            Log.d(TAG, "Re-recording action $it")
        }

        frameHandler = FeatureExtractorFrameHandler(
            this,
            this,
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordFacialExpressionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordNeutralExpression = MainModel.getInstance().facialExpressionActions.isEmpty()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })

        setUi()
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        frameHandlerThread = Thread(frameHandler)
        frameHandlerThread.start()

        val persistenceManager = PersistenceManager(requireContext())
        if (persistenceManager.getValue(RECORDING_FIRST_TIME_KEY, true) as Boolean) {
            showInfo()
            persistenceManager.setValue(RECORDING_FIRST_TIME_KEY, false)
        } else if (!viewModel.cameraPermission.value!!) {
            askCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        cameraProvider?.unbindAll()
        frameHandler.stop()
        frameHandlerThread.interrupt()
    }

    private fun askCameraPermission() {
        PermissionsHandler.askCameraPermission(
            requireActivity(),
            FacialExpressionActivity.CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun setUi() {
        if (isReRecording)
            binding.expressionType.text = getString(R.string.feraction_recorded_expression_message)
        else if (recordNeutralExpression)
            binding.expressionType.text = getString(R.string.feraction_neutral_expression_message)

        binding.startAcquisitionButton.root.isEnabled = false

        binding.startAcquisitionButton.root.setOnClickListener {
            acquireExpression()
            binding.startAcquisitionButton.root.isEnabled = false
        }
    }

    private fun setObservers() {
        viewModel.cameraPermission.observe(viewLifecycleOwner) {
            if (it) {
                if (infoDialog?.isShowing == true)
                    infoDialog?.dismiss()
                startCameraCapture()
            }
        }

        viewModel.acquiredFramesCnt.observe(viewLifecycleOwner) { cnt ->
            binding.progressBar.setProgress(
                100 * cnt / FacialExpressionAction.FRAMES_X_EXPRESSION,
                true
            )
            if (cnt == FacialExpressionAction.FRAMES_X_EXPRESSION)
                onFramesAcquired()
        }
    }

    private fun onFramesAcquired() {
        if (viewModel.isDuplicate(actionId))
            navigateToDuplicateFacialExpressionFragment()
        else if (isReRecording)
            returnFramesToInfoFacialExpressionFragment()
        else if (recordNeutralExpression)
            navigateToNeutralFacialExpressionFragment()
        else
            navigateToDefineFacialExpressionFragment()
    }

    private fun acquireExpression() {
        Log.d(TAG, "acquireExpression")
        frameHandler.startFeatureExtraction()
    }

    private fun startCameraCapture() {
        Log.d(TAG, "startCameraCapture")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
            } catch (e: ExecutionException) {
                Log.e(TAG, "Error getting camera provider: " + e.localizedMessage)
            }

            if (cameraProvider == null) {
                showCameraInitializationErrorMessage()
                navigateBack()
                return@addListener
            }

            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(frameWidth, frameHeight))
                .build()

            analysisUseCase.setAnalyzer(
                ContextCompat.getMainExecutor(requireContext())
            ) {
                try {
                    processFrame(it)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to process frame: ${e.localizedMessage}")
                    it.close()
                }
            }

            if (!frameHandler.init(requireContext())) {
                showCameraInitializationErrorMessage()
                navigateBack()
                return@addListener
            }

            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(requireActivity(), cameraSelector, analysisUseCase)
            binding.loadingCameraProgressBar.visibility = View.GONE
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processFrame(imageProxy: ImageProxy) {
        if (imageProxy.image == null)
            imageProxy.close()
        else {
            //Log.d(TAG, "Frame captured: ${imageProxy.width} x ${imageProxy.height}")
            frameHandler.onImageProxy(imageProxy)
        }
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
            binding.startAcquisitionButton.root.isEnabled = false
        }
    }

    override fun onPositioningRestored() {
        binding.root.post {
            binding.missingFaceErrorBox.root.visibility = View.GONE
            binding.missingLandmarkErrorBox.root.visibility = View.GONE
            binding.startAcquisitionButton.root.isEnabled = true
        }
    }

    override fun onExpressionFrame(frame: Frame) {
        Log.d(TAG, "onExpressionFrame: ${frame.features}")
        binding.root.post { viewModel.addFrame(frame) }
    }

    private fun returnFramesToInfoFacialExpressionFragment() {
        MainModel.getInstance().tempFacialExpressionActionFrames = viewModel.frames
        requireActivity().setResult(InfoFacialExpressionActionFragment.RESULT_CODE)
        requireActivity().finishAndRemoveTask()
    }

    private fun showCameraInitializationErrorMessage() {
        Toast.makeText(
            requireContext(),
            getString(R.string.feraction_camera_initialization_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateBack() {
        requireActivity().finish()
    }

    private fun navigateToDefineFacialExpressionFragment() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_recordFacialExpressionFragment_to_defineFacialExpressionFragment
            )
    }

    private fun navigateToDuplicateFacialExpressionFragment() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_recordFacialExpressionFragment_to_duplicateFacialExpressionFragment,
                bundleOf(
                    DuplicateFacialExpressionFragment.RERECORDING_KEY to isReRecording
                )
            )
    }

    private fun navigateToNeutralFacialExpressionFragment() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_recordFacialExpressionFragment_to_neutralFacialExpressionFragment
            )
    }

    override fun showInfo() {
        Log.d(TAG, "showInfo")

        val dialog = BaseDialog(requireContext()).apply {
            titleText = R.string.feraction_record_info_title
            subTitleText = R.string.feraction_record_info_msg
            textPrimaryButton = android.R.string.ok
            setColorsByActionType(Action.ActionType.FACIAL_EXPRESSION)
        }
        dialog.listener = object : BaseDialog.BaseDialogListener {
            override fun onPrimaryButtonClicked() {
                if (!viewModel.cameraPermission.value!!)
                    askCameraPermission()
                else
                    infoDialog?.dismiss()
            }

            override fun onSecondaryButtonClicked() {}
            override fun onCheckClicked(checked: Boolean) {}
        }

        dialog.show()
        infoDialog = dialog
    }
}