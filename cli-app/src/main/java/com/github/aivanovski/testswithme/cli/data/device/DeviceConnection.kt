package com.github.aivanovski.testswithme.cli.data.device

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.network.GatewayClient
import com.github.aivanovski.testswithme.cli.entity.ConnectionState
import com.github.aivanovski.testswithme.cli.entity.exception.DeviceConnectionException
import com.github.aivanovski.testswithme.utils.StringUtils.SPACE
import dadb.Dadb
import kotlinx.coroutines.flow.MutableStateFlow

class DeviceConnection(
    val api: GatewayClient,
    initialState: ConnectionState,
    private val device: Dadb,
    private val portForwardingConnection: AutoCloseable
) : AutoCloseable {

    @Volatile
    private var isClosed = false

    val state = MutableStateFlow(initialState)

    override fun close() {
        if (isClosed) {
            return
        }

        println("Closing connection...")
        isClosed = true
        try {
            portForwardingConnection.close()
            device.close()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun startHttpServer(): Either<DeviceConnectionException, Unit> =
        sendBroadcast(
            packageName = APPLICATION_PACKAGE_NAME,
            receiverName = RECEIVER_PATH,
            data = mapOf(
                "command" to "start"
            )
        )

    fun stopHttpServer(): Either<DeviceConnectionException, Unit> =
        sendBroadcast(
            packageName = APPLICATION_PACKAGE_NAME,
            receiverName = RECEIVER_PATH,
            data = mapOf(
                "command" to "start"
            )
        )

    private fun sendBroadcast(
        packageName: String,
        receiverName: String,
        data: Map<String, String>
    ): Either<DeviceConnectionException, Unit> =
        either {
            val command = buildString {
                append("am broadcast -n $packageName/$receiverName")
                append(SPACE)
                for ((key, value) in data) {
                    append("--es \"$key\" \"$value\"")
                }
            }

            val response = device.shell(command)
            if (response.exitCode != 0) {
                raise(DeviceConnectionException("Failed to send broadcast: $response"))
            }
        }

    companion object {
        private const val APPLICATION_PACKAGE_NAME = "com.github.aivanovski.testswithme.android"
        private const val RECEIVER_PATH =
            "$APPLICATION_PACKAGE_NAME.domain.driverServer.GatewayCommandReceiver"
    }
}