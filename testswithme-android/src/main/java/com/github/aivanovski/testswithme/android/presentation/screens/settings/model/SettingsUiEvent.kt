package com.github.aivanovski.testswithme.android.presentation.screens.settings.model

sealed interface SettingsUiEvent {
    data object ShowAccessibilityServices : SettingsUiEvent
}