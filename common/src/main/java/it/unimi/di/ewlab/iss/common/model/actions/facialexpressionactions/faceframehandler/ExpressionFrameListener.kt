package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler

import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame

interface ExpressionFrameListener {
    fun onExpressionFrame(frame: Frame)
}