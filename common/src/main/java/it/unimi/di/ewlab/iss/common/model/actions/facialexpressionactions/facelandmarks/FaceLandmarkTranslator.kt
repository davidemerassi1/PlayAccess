package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks

import android.util.Size

interface FaceLandmarkTranslator<T> {
    companion object {
        val LANDMARKS_CNT = 478
    }

    fun translate(landmarks: List<T>, frameSize: Size): List<FaceLandmark>
}