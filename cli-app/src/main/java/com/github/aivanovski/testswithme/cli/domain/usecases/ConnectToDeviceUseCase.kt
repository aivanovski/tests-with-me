package com.github.aivanovski.testswithme.cli.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints.PORT
import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.data.network.GatewayClient
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.entity.exception.DeviceConnectionException
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToConnectToGatewayServer
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToFindDeviceException
import dadb.Dadb

class ConnectToDeviceUseCase(
    private val apiClient: GatewayClient
) {

    suspend fun connectToDevice(
        printer: OutputPrinter
    ): Either<DeviceConnectionException, DeviceConnection> =
        either {
            val devices = Dadb.list()
            if (devices.isEmpty()) {
                raise(FailedToFindDeviceException())
            }

            val device = devices.first()

            val portForwarding = try {
                device.tcpForward(PORT, PORT)
            } catch (exception: Exception) {
                raise(DeviceConnectionException(cause = exception))
            }

            val connection = DeviceConnection(
                api = apiClient,
                device = device,
                portForwardingConnection = portForwarding
            )

            val deviceName = device.toString()
            printer.printLine("Connecting to device: $deviceName")
            if (!isDriverHttpServerAvailable(connection)) {
                connection.startHttpServer().bind()
            }

            if (!isDriverHttpServerAvailable(connection)) {
                raise(FailedToConnectToGatewayServer())
            }

            printer.printLine("Connected")

            connection
        }

    private suspend fun isDriverHttpServerAvailable(connection: DeviceConnection): Boolean {
        val getStatusResult = connection.api.getStatus()
        return getStatusResult.isRight()
    }
}