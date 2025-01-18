package com.github.aivanovski.testswithme.cli.entity

import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.presentation.main.model.DeviceState

data class ConnectionAndState(
    val connection: DeviceConnection,
    val state: DeviceState
)