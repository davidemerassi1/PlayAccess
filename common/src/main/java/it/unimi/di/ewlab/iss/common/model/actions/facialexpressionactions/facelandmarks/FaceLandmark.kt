package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks

import it.unimi.di.ewlab.iss.common.utils.Position3D

data class FaceLandmark(
    val framePosition: Position3D,
    val facePosition: Position3D,
    val type: FaceLandmarkType?
)