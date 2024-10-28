package com.github.aivanovski.testswithme.cli.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.adb.AdbContext
import com.github.aivanovski.testswithme.cli.data.adb.command.GetDevicesCommand
import com.github.aivanovski.testswithme.cli.data.process.ProcessExecutor
import com.github.aivanovski.testswithme.cli.entity.exception.AppException
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToFindDeviceException

class ConnectToDeviceUseCase(
    private val processExecutor: ProcessExecutor
) {

    fun connectToDevice(): Either<AppException, AdbContext> =
        either {
            val defaultContext = AdbContext(processExecutor, device = null)
            val devices = defaultContext.execute(GetDevicesCommand()).bind()
            if (devices.isEmpty()) {
                raise(FailedToFindDeviceException())
            }

            val device = devices.first()

            AdbContext(processExecutor, device = device)
        }
}