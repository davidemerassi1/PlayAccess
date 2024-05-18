package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.util.Log
import android.util.Size
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmark
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmarkTranslator
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.FaceLandmarksDrawer
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.MediaPipeFaceLandmarkTranslator
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks.MissingFaceLandmarkException
import it.unimi.di.ewlab.iss.common.utils.Utils

@ExperimentalGetImage abstract class FaceFrameHandler(
    private val cameraFrameListener: CameraFrameListener?,
    private val wrongPositioningListener: WrongPositioningListener?,
) : Runnable {

    companion object {
        private const val TAG = "FaceFrameHandler"
        private const val MP_FACE_LANDMARKER_TASK = "face_landmarker.task"

        private const val DEFAULT_FACE_DETECTION_CONFIDENCE = 0.5F
        private const val DEFAULT_FACE_TRACKING_CONFIDENCE = 0.5F
        private const val DEFAULT_FACE_PRESENCE_CONFIDENCE = 0.5F
    }

    abstract val operatorThreadName: String
    private val maxFaces = 1

    private lateinit var frame: ImageProxy
    private var frameBitmap: Bitmap? = null

    private var lock = Object()
    private var isProcessingFrame = false
    protected var canProcessLandmarks = false
    var drawLandmarks = true

    private var landmarkExtractor: FaceLandmarker? = null
    private val landmarkTranslator: FaceLandmarkTranslator<NormalizedLandmark> = MediaPipeFaceLandmarkTranslator()
    private val landmarksDrawer = FaceLandmarksDrawer()

    private lateinit var landmarksHandler: Handler
    private lateinit var context: Context;

    open fun init(context: Context): Boolean {
        Log.d(TAG, "init")
        this.context = context.applicationContext

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val configurationInfo: ConfigurationInfo = activityManager!!.deviceConfigurationInfo

        Log.d(TAG, "OpenGL ES version: ${configurationInfo.glEsVersion}")
        if (configurationInfo.reqGlEsVersion < 0x30001)
            return false

        val handlerThread = HandlerThread(operatorThreadName)
        handlerThread.start()
        val operatorThreadLooper = handlerThread.looper
        landmarksHandler = Handler(operatorThreadLooper)

        return initLandmarkExtractor(context)
    }

    fun stop() {
        if (landmarkExtractor == null)
            return

        Log.d(TAG, "stop")

        landmarkExtractor?.let {
            it.close()
            landmarkExtractor = null
        }

        landmarksHandler.looper.thread.interrupt()

        isProcessingFrame = false
    }

    private fun initLandmarkExtractor(context: Context): Boolean {
        val baseOptionBuilder = BaseOptions.builder()
                .setDelegate(Delegate.GPU)
                .setModelAssetPath(MP_FACE_LANDMARKER_TASK)

        try {
            val baseOptions = baseOptionBuilder.build()

            val options =
                FaceLandmarker.FaceLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinFaceDetectionConfidence(DEFAULT_FACE_DETECTION_CONFIDENCE)
                    .setMinTrackingConfidence(DEFAULT_FACE_TRACKING_CONFIDENCE)
                    .setMinFacePresenceConfidence(DEFAULT_FACE_PRESENCE_CONFIDENCE)
                    .setNumFaces(maxFaces)
                    .setRunningMode(RunningMode.LIVE_STREAM)
                    .setResultListener(this::onFaceDetected)
                    .setErrorListener(this::onError)
                    .build()

            landmarkExtractor = FaceLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            Log.e(
                TAG, "MediaPipe failed to load the task with error: " + e
                    .message
            )
            return false
        } catch (e: RuntimeException) {
            // This occurs if the model being used does not support GPU
            Log.e(
                TAG,
                "Face Landmarker failed to load model with error: " + e.message
            )
            return false
        }
        return true
    }

    private fun onDetectionError(err: WrongPositioningListener.PositioningError) {
        cameraFrameListener?.onCameraFrameAnalyzed(frameBitmap!!)
        wrongPositioningListener?.onWrongPositioning(err)
        isProcessingFrame = false
    }

    private fun onError(error: RuntimeException?) {
        Log.e(TAG, error?.message ?: "An unknown error has occurred")
    }

    private fun onFaceDetected(faces: FaceLandmarkerResult?, mpImage: MPImage?) {
        Log.d(TAG, "onFaceDetected")
        mpImage?.close()

        if (faces == null || faces.faceLandmarks().isEmpty()) {
            onDetectionError(WrongPositioningListener.PositioningError.NO_FACE_DETECTED)
            return
        }

        //Log.d(TAG, "Faces detected: ${faces.faceLandmarks().size}")
        //Log.d(TAG, "Landmarks detected: ${faces.faceLandmarks()[0].size}")
        val landmarks: List<FaceLandmark>
        try {
            landmarks = landmarkTranslator.translate(
                faces.faceLandmarks()[0],
                Size(frameBitmap!!.width, frameBitmap!!.height)
            )
        } catch (e: MissingFaceLandmarkException) {
            onDetectionError(WrongPositioningListener.PositioningError.MISSING_FACE_LANDMARK)
            return
        }
        wrongPositioningListener?.onPositioningRestored()

        val landmarksBitmap =
            if (drawLandmarks) landmarksDrawer.drawSkeleton(
                frameBitmap!!,
                landmarks
            ) else frameBitmap
        cameraFrameListener?.onCameraFrameAnalyzed(landmarksBitmap!!)

        if (canProcessLandmarks) {
            landmarksHandler.post {
                processLandmarks(landmarks, landmarksBitmap!!)
                isProcessingFrame = false
            }
        } else {
            isProcessingFrame = false
        }
    }

    override fun run() {
        Log.d(TAG, "FrameHandler started")

        while (true) {
            synchronized(lock) {
                try {
                    lock.wait()
                } catch (e: InterruptedException) {
                    Log.d(TAG, "FrameHandler stopped")
                    return
                }
            }

            processImageProxy()
        }
    }

    private fun processImageProxy() {
        //Log.d(TAG, "processImageProxy")

        if (frame.image == null) {
            isProcessingFrame = false
            return
        }

        val frameTime = SystemClock.uptimeMillis()

        val image = frame.image!!
        val rotationDegrees = frame.imageInfo.rotationDegrees

        frameBitmap = Utils.imageToBitmap(context, image, rotationDegrees.toFloat())
        frame.close()

        val mpImage = BitmapImageBuilder(frameBitmap).build()
        detectAsync(mpImage, frameTime)
    }

    private fun detectAsync(mpImage: MPImage, frameTime: Long) {
        landmarkExtractor?.detectAsync(mpImage, frameTime)
        // As we're using running mode LIVE_STREAM, the landmark result will
        // be returned in returnLivestreamResult function
    }

    @Synchronized
    fun onImageProxy(imageProxy: ImageProxy) {
        checkInitialized()
        //Log.d(TAG, "onImageProxy; is processing: $isProcessingFrame")

        if (!isProcessingFrame && imageProxy.image != null) {

            isProcessingFrame = true
            this.frame = imageProxy
            synchronized(lock) { lock.notify() }

        } else imageProxy.close()
    }

    private fun checkInitialized() {
        if (landmarkExtractor == null)
            throw IllegalStateException("FaceFrameHandler not initialized; call init(Context) method first")
    }

    protected abstract fun processLandmarks(
        landmarks: List<FaceLandmark>,
        landmarksBitmap: Bitmap,
    )
}