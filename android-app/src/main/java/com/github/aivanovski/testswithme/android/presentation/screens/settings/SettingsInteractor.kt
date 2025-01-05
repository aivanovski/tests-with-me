package com.github.aivanovski.testswithme.android.presentation.screens.settings

import com.github.aivanovski.testswithme.android.data.api.HttpRequestExecutor
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.driverServer.GatewayServer
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.domain.usecases.ClearDataUseCase
import com.github.aivanovski.testswithme.android.entity.DriverServiceState

class SettingsInteractor(
    private val server: GatewayServer,
    private val httpExecutor: HttpRequestExecutor,
    private val settings: Settings,
    private val clearDataUseCase: ClearDataUseCase
) {

    fun isDriverRunning(): Boolean =
        (FlowRunnerManager.getDriverState() == DriverServiceState.RUNNING)

    fun isGatewayRunning(): Boolean = server.isRunning()

    fun startGatewayServer() = server.start()

    fun stopGatewayServer() = server.stop()

    fun setSslVerificationEnabled(isSslVerificationEnabled: Boolean) {
        settings.isSslVerificationDisabled = !isSslVerificationEnabled
        httpExecutor.rebuild(isSslVerificationEnabled = isSslVerificationEnabled)
    }

    fun clearAccountRelatedData() {
        clearDataUseCase.clearUserData()
    }
}