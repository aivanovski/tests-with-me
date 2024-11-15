package com.github.aivanovski.testswithme.android.presentation.screens.settings

import com.github.aivanovski.testswithme.android.domain.driverServer.GatewayServer
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.entity.DriverServiceState

class SettingsInteractor(
    private val server: GatewayServer
) {

    fun isDriverRunning(): Boolean =
        (FlowRunnerManager.getDriverState() == DriverServiceState.RUNNING)

    fun isGatewayRunning(): Boolean = server.isRunning()

    fun startGatewayServer() = server.start()

    fun stopGatewayServer() = server.stop()
}