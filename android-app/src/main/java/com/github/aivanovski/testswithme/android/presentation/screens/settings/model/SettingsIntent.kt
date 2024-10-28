package com.github.aivanovski.testswithme.android.presentation.screens.settings.model

sealed interface SettingsIntent {

    data object Initialize : SettingsIntent

    data class OnSslValidationStateChanged(
        val isEnabled: Boolean
    ) : SettingsIntent
}