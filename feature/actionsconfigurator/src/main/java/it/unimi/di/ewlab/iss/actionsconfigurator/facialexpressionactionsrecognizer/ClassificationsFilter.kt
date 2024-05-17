package it.unimi.di.ewlab.iss.actionsconfigurator.facialexpressionactionsrecognizer

import it.unimi.di.ewlab.iss.common.model.Configuration
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Classifier
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.ClassificationListener
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler.WrongPositioningListener

class ClassificationsFilter(
    precision: Configuration.Settings.FacialExpressionPrecision,
    private val filterListener: FilterListener
    //private val wrongPositioningListener: WrongPositioningListener?
) : ClassificationListener, WrongPositioningListener {

    private val buffer = ExpressionsBuffer(precision)

    override fun onClassification(classification: Classifier.ClassifierResult) {
        if (classification.certain) {
            filterListener.onClassification(classification.label)
            buffer.clear(classification.label)
        } else {
            buffer.update(classification.label)
            filterListener.onClassification(buffer.expression)
        }
    }

    override fun onWrongPositioning(err: WrongPositioningListener.PositioningError) {
        buffer.update(ExpressionsBuffer.NOT_RECOGNIZED_ID)
        filterListener.onClassification(buffer.expression)
        //wrongPositioningListener?.onWrongPositioning(err)
    }

    override fun onPositioningRestored() {
        //wrongPositioningListener?.onPositioningRestored()
    }
}
