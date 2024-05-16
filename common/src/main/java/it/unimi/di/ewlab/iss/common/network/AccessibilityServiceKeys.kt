package it.unimi.di.ewlab.iss.common.network

const val AccessibilityServiceAction = "custom_intent"

enum class AccessibilityServiceKeys {
    StartGameConfiguration,
    GameStarted,
    RecordExternalButtonAction,
    DisableService,
    StartRecognizers,
    StopRecognizers,
    PreferencesUpdated,
    OpenSettings,
    ChangeConfiguration,
    WaitForGame,
    SelectedConfiguration,
    EnableNotifications,
}