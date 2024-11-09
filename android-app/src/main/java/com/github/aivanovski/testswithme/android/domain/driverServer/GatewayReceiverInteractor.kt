package com.github.aivanovski.testswithme.android.domain.driverServer

import com.github.aivanovski.testswithme.android.domain.driverServer.model.GatewayCommand

class GatewayReceiverInteractor(
    private val server: GatewayServer
) {

    fun processCommand(command: GatewayCommand) {
        when (command) {
            is GatewayCommand.Start -> {
                if (!server.isRunning()) {
                    server.start()
                }
            }

            is GatewayCommand.Stop -> {
                if (server.isRunning()) {
                    server.stop()
                }
            }
        }
    }
}