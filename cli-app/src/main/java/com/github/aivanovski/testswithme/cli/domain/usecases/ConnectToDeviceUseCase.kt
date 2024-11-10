package com.github.aivanovski.testswithme.cli.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.GatewayEndpoints.PORT
import com.github.aivanovski.testswithme.android.driverServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.data.network.GatewayClient
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.entity.ConnectionState
import com.github.aivanovski.testswithme.cli.entity.exception.DeviceConnectionException
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToConnectToGatewayServer
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToFindDeviceException
import dadb.Dadb
import kotlinx.coroutines.delay

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
                initialState = ConnectionState(
                    isConnected = false,
                    isDriverReady = false
                ),
                device = device,
                portForwardingConnection = portForwarding
            )

            val deviceName = device.toString()
            printer.printLine("Connecting to device: $deviceName")

            var status = getStatus(connection)
            if (status == null) {
                connection.startHttpServer().bind()

                delay(1000L)

                status = getStatus(connection)
            }

            if (status == null) {
                raise(FailedToConnectToGatewayServer())
            }

            connection.state.value = ConnectionState(
                isConnected = true,
                isDriverReady = status.driverStatus == DriverStatusDto.RUNNING
            )

            connection
        }

    private suspend fun getStatus(connection: DeviceConnection): GetStatusResponse? {
        val getStatusResult = connection.api.getStatus()
        return getStatusResult.getOrNull()
    }
}