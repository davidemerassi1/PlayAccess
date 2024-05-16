package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler

interface WrongPositioningListener {
    fun onWrongPositioning(err: PositioningError)
    fun onPositioningRestored()

    enum class PositioningError {
        NO_FACE_DETECTED,
        MISSING_FACE_LANDMARK
    }
}