package it.unimi.di.ewlab.iss.common.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.unimi.di.ewlab.iss.common.model.Configuration
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.storage.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    enum class BooleanSetting {
        ENABLE_AUDIO,
        SHOW_EVENTS_ON_SCREEN,
        SHOW_FEEDBACK_SCREEN,
        SHOW_RECOGNIZED_FACIAL_EXPRESSIONS,
        SHOW_EXPRESSION_LANDMARKS
    }

    private val booleanPreferences = hashMapOf<BooleanSetting, Boolean>()
    var fePrecision: Configuration.Settings.FacialExpressionPrecision = Configuration.Settings.FE_PRECISION_DEFAULT
        private set

    private val _disablePipSettings = MutableLiveData<Boolean>(null)
    val disablePipSettings: LiveData<Boolean> = _disablePipSettings

    private var configuration: Configuration? = null

    fun loadConfiguration(configuration: Configuration) {
        this.configuration = configuration
        booleanPreferences[BooleanSetting.ENABLE_AUDIO] = configuration.settings.enableAudio
        booleanPreferences[BooleanSetting.SHOW_EVENTS_ON_SCREEN] = configuration.settings.showEventsOnScreen
        booleanPreferences[BooleanSetting.SHOW_FEEDBACK_SCREEN] = configuration.settings.showFeedbackScreen
        booleanPreferences[BooleanSetting.SHOW_RECOGNIZED_FACIAL_EXPRESSIONS] = configuration.settings.showRecognizedFacialExpressions
        booleanPreferences[BooleanSetting.SHOW_EXPRESSION_LANDMARKS] = configuration.settings.showExpressionLandmarks
        fePrecision = configuration.settings.facialExpressionsPrecision
    }

    fun updateBooleanPreference(preference: BooleanSetting, value: Boolean) {
        if (preference == BooleanSetting.SHOW_FEEDBACK_SCREEN && value != booleanPreferences[preference])
            _disablePipSettings.value = true
        booleanPreferences[preference] = value
    }

    fun setFePrecision(precision: Int) {
        if (precision < 0 || precision >= 4)
            throw IllegalArgumentException("precision value must be between 0 and 2")
        fePrecision = Configuration.Settings.FacialExpressionPrecision.values()[precision]
    }

    fun getBooleanPreference(preference: BooleanSetting): Boolean {
        return booleanPreferences[preference]!!
    }

    fun saveSettings() {
        configuration?.let {it.settings.apply {
            enableAudio = booleanPreferences[BooleanSetting.ENABLE_AUDIO]!!
            showEventsOnScreen = booleanPreferences[BooleanSetting.SHOW_EVENTS_ON_SCREEN]!!
            showFeedbackScreen = booleanPreferences[BooleanSetting.SHOW_FEEDBACK_SCREEN]!!
            showRecognizedFacialExpressions = booleanPreferences[BooleanSetting.SHOW_RECOGNIZED_FACIAL_EXPRESSIONS]!!
            showExpressionLandmarks = booleanPreferences[BooleanSetting.SHOW_EXPRESSION_LANDMARKS]!!
            facialExpressionsPrecision = fePrecision
        }}
        MainModel.getInstance().writeGamesJson()
    }
}