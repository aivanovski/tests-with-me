package com.github.aivanovski.testswithme.cli.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.GatewayEndpoints.PORT
import com.github.aivanovski.testswithme.android.driverServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.data.network.GatewayClient
import com.github.aivanovski.testswithme.cli.entity.ConnectionAndState
import com.github.aivanovski.testswithme.cli.entity.exception.DeviceConnectionException
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToConnectToGatewayServer
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToFindDeviceException
import com.github.aivanovski.testswithme.cli.presentation.main.model.DeviceState
import dadb.Dadb
import kotlinx.coroutines.delay

class ConnectToDeviceUseCase(
    private val apiClient: GatewayClient
) {

    suspend fun connectToDevice(): Either<DeviceConnectionException, ConnectionAndState> =
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

            var status = getStatus(connection)
            if (status == null) {
                connection.startHttpServer().bind()

                delay(1000L)

                status = getStatus(connection)
            }

            if (status == null) {
                raise(FailedToConnectToGatewayServer())
            }

            val deviceState = DeviceState.Connected(
                isConnected = true,
                isDriverReady = (status.driverStatus == DriverStatusDto.RUNNING)
            )

            ConnectionAndState(connection, deviceState)
        }

    private suspend fun getStatus(connection: DeviceConnection): GetStatusResponse? {
        val getStatusResult = connection.api.getStatus()
        return getStatusResult.getOrNull()
    }
}