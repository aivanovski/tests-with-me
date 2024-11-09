package com.github.aivanovski.testswithme.android.presentation.screens.settings.model

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val isLoading: Boolean = true,
    val isSslValidationChecked: Boolean = false,
    val isGatewayChecked: Boolean = false,
    val isGatewaySwitchEnabled: Boolean = false,
    val gatewayDescription: String = ""
)