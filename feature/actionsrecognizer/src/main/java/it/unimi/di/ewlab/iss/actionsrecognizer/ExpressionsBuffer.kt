package it.unimi.di.ewlab.iss.actionsrecognizer

import android.util.Log
import it.unimi.di.ewlab.iss.common.model.Configuration

class ExpressionsBuffer(
    private val precision: Configuration.Settings.FacialExpressionPrecision
) {
    companion object {
        private const val TAG = "ExpressionsBuffer"
        const val NOT_RECOGNIZED_ID = -1
    }

    private val queue = ArrayDeque<Int>()

    var expression = NOT_RECOGNIZED_ID
        private set

    private val minElems = precision.bufferSize / 2 + 1

    private fun ArrayDeque<Int>.isFull() = size == precision.bufferSize

    init {
        Log.d(TAG, "Buffer dimension: ${precision.bufferSize}")
    }

    fun clear(newExpression: Int) {
        queue.clear()
        expression = newExpression
    }

    fun update(expression: Int) {
        if (queue.isFull())
            queue.removeFirstOrNull()
        queue.add(expression)
        if (queue.size >= minElems)
            updateExpression()
    }

    private fun updateExpression() {
        // mappa espressione (actionId) -> occorrenze
        val occurrences = queue.groupingBy { it }.eachCount()
        val maxExpr = occurrences.maxByOrNull { it.value }?.key ?: NOT_RECOGNIZED_ID
        if (occurrences[maxExpr]!! >= minElems) {
            expression = maxExpr
            queue.clear()
        }
    }
}
