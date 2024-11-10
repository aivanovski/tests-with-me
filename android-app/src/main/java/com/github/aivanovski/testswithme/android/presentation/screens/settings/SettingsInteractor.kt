package com.github.aivanovski.testswithme.android.presentation.screens.settings

import com.github.aivanovski.testswithme.android.domain.driverServer.GatewayServer

class SettingsInteractor(
    private val server: GatewayServer
) {

    fun isGatewayRunning(): Boolean = server.isRunning()

    fun startGatewayServer() = server.start()

    fun stopGatewayServer() = server.stop()
}