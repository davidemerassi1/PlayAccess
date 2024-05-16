package it.unimi.di.ewlab.iss.common.model.actions

import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame

class FacialExpressionAction: Action, Iterable<Frame> {

    companion object {
        const val FRAMES_X_EXPRESSION = 5
        private val TYPE = ActionType.FACIAL_EXPRESSION
    }

    var frames: List<Frame> = listOf()
        set(value) {
            if (value.size != FRAMES_X_EXPRESSION)
                throw IllegalArgumentException("Missing frames ${value.size}/$FRAMES_X_EXPRESSION")
            for ((i, frame) in value.withIndex())
                if (i > 0) frame.clearBitmap()
            field = value.toList()
            means = getMeans(frames)
        }
    @Transient
    var means: List<Float> = listOf()
        get() {
            if (field.isEmpty())
                field = getMeans(frames)
            return field
        }
        private set

    internal constructor() : super(TYPE)

    constructor(actionId: Int, name: String, frames: List<Frame>) : super(actionId, name, TYPE) {
        if (frames.size != FRAMES_X_EXPRESSION)
            throw IllegalArgumentException("Missing frames ${frames.size}/$FRAMES_X_EXPRESSION")
        for ((i, frame) in frames.withIndex())
            if (i > 0) frame.clearBitmap()
        this.frames = frames.toList()
        this.means = getMeans(frames)
    }

    private fun getMeans(frames: List<Frame>): List<Float> {
        val means = mutableListOf<Float>()
        for (feature in frames[0].features.indices) {
            var sum = 0F
            for (frame in frames) {
                sum += frame.features[feature]!!
            }
            means.add(sum / frames.size)
        }
        return means
    }

    override fun iterator(): Iterator<Frame> {
        return frames.iterator()
    }
}