package com.github.aivanovski.testswithme.cli.extensions

import com.github.aivanovski.testswithme.cli.presentation.main.model.DeviceState

fun DeviceState.isReadyToStartTest(): Boolean {
    return this is DeviceState.Connected &&
        this.isConnected &&
        this.isDriverReady
}