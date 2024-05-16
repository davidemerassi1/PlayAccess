package it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.FacialExpressionAction
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.Frame
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Classifier
import it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.classification.Prototypical

class FacialExpressionViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "FacialExpressionViewModel"
    }

    private val classifier: Classifier = Prototypical()
    var duplicateAction: FacialExpressionAction? = null
        private set

    var name = ""
        private set

    private val neutralExpressionName =
        application.applicationContext.getString(R.string.feraction_neutral_expression_name)

    private val _cameraPermission = MutableLiveData(false)
    val cameraPermission: LiveData<Boolean> = _cameraPermission

    private val _acquiredFramesCnt = MutableLiveData(0)
    val acquiredFramesCnt: LiveData<Int> = _acquiredFramesCnt

    private val _frames = mutableListOf<Frame>()
    val frames: List<Frame>
        get() = _frames

    init {
        classifier.setPrecision(Prototypical.DEFAULT_RADIUS * 0.75F)
    }

    fun clear() {
        name = ""
        _frames.clear()
        _acquiredFramesCnt.value = 0
    }

    fun setName(name: String) {
        this.name = name
        checkActionFields()
    }

    fun addFrame(frame: Frame) {
        Log.d(TAG, "addFrame")
        if (_frames.size == FacialExpressionAction.FRAMES_X_EXPRESSION) return
        _frames.add(frame)
        _acquiredFramesCnt.value = _frames.size
        checkActionFields()
    }

    private fun checkActionFields(): Boolean {
        return name.isNotEmpty() &&
                _frames.size == FacialExpressionAction.FRAMES_X_EXPRESSION &&
                MainModel.getInstance().isValidActionName(name)
    }

    fun saveAction() {
        Log.d(TAG, "saveAction")
        if (!checkActionFields())
            throw IllegalStateException("Not all action fields are set")

        val action = FacialExpressionAction(MainModel.getInstance().nextActionId, name, _frames)
        MainModel.getInstance().addAction(action)
        MainModel.getInstance().writeActionsJson()
    }

    fun saveNeutralFacialExpressionAction() {
        Log.d(TAG, "saveNeutralFacialExpressionAction")
        if (_frames.size != FacialExpressionAction.FRAMES_X_EXPRESSION)
            throw IllegalStateException("frames not set")

        MainModel.getInstance().setNeutralFacialExpressionAction(
            FacialExpressionAction(
                MainModel.NEUTRAL_FACIAL_EXPRESSION_ACTION_ID,
                neutralExpressionName,
                _frames
            )
        )
        MainModel.getInstance().writeActionsJson()
    }

    fun isDuplicate(actionIdNotToBeConsidered: Int?): Boolean {
        if (frames.size != FacialExpressionAction.FRAMES_X_EXPRESSION)
            throw IllegalStateException("Missing frames ${frames.size}/${FacialExpressionAction.FRAMES_X_EXPRESSION}")

        val actions = MainModel.getInstance().facialExpressionActions

        actionIdNotToBeConsidered?.let {
            actions.remove(
                MainModel.getInstance().getActionById(it)
            )
        }

        if (!classifier.train(actions)) {
            return false
        }

        // < 0 -> nessuna classificazione -> ok
        // >= 0 -> duplicato trovato -> !ok
        val duplicate = classifier.classify(frames[0].features).label
        if (duplicate < 0)
            return false

        duplicateAction =
            MainModel.getInstance().getActionById(duplicate) as FacialExpressionAction

        return true
    }

    fun setCameraPermission(cameraPermission: Boolean) {
        this._cameraPermission.value = cameraPermission
    }
}