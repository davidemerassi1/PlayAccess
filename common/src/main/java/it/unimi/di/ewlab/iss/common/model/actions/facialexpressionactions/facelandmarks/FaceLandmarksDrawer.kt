package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

class FaceLandmarksDrawer {

    companion object {
        private const val TAG = "FaceLandmarkDrawer"
    }

    private val markerDimension = 1.0f
    private val basePaint = Paint()
    private val mouthPaint = Paint()
    private val nosePaint = Paint()
    private val eyesPaint = Paint()

    init {
        basePaint.color = Color.GREEN
        basePaint.style = Paint.Style.FILL
        mouthPaint.color = Color.YELLOW
        mouthPaint.style = Paint.Style.FILL
        nosePaint.color = Color.BLUE
        nosePaint.style = Paint.Style.FILL
        eyesPaint.color = Color.RED
        eyesPaint.style = Paint.Style.FILL
    }

    fun drawSkeleton(image: Bitmap, landmarks: List<FaceLandmark>): Bitmap {
        if (landmarks.size != FaceLandmarkTranslator.LANDMARKS_CNT) {
            Log.e(TAG, "drawSkeleton: missing landmarks ${landmarks.size}/${FaceLandmarkTranslator.LANDMARKS_CNT}")
            throw MissingFaceLandmarkException()
        }

        if (!image.isMutable) {
            Log.w(TAG, "Bitmap image must be mutable")
            return image
        }

        val canvas = Canvas(image)
        canvas.drawBitmap(image, 0f, 0f, null)
        drawLandmarks(canvas, landmarks)
        return image
    }

    private fun drawLandmarks(canvas: Canvas, landmarks: List<FaceLandmark>) {
        for (landmark in landmarks) {
            drawCircle(canvas, landmark)
        }
    }

    private fun drawCircle(canvas: Canvas, landmark: FaceLandmark) {
        canvas.drawCircle(
            landmark.framePosition.x,
            landmark.framePosition.y,
            markerDimension,
            basePaint
        )
    }
}