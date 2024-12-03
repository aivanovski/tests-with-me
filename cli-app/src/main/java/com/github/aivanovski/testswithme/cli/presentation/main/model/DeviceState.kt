package com.github.aivanovski.testswithme.cli.presentation.main.model

sealed interface DeviceState {

    data object Connecting : DeviceState

    data class Connected(
        val isConnected: Boolean,
        val isDriverReady: Boolean
    ) : DeviceState
}