package com.github.aivanovski.testswithme.android.domain.gatewayServer

import com.github.aivanovski.testswithme.android.domain.gatewayServer.model.GatewayCommand

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