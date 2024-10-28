package com.github.aivanovski.testswithme.cli.data.adb

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.process.ProcessExecutor
import com.github.aivanovski.testswithme.cli.entity.AdbDevice
import com.github.aivanovski.testswithme.cli.entity.exception.AdbException

class AdbExecutor(
    private val processExecutor: ProcessExecutor,
    private val device: AdbDevice?
) {

    fun run(command: String): Either<AdbException, String> =
        either {
            val commandWithDevice = if (device != null) {
                command.replace("adb ", "adb -s ${device.id} ")
            } else {
                command
            }

            processExecutor.run(commandWithDevice)
                .mapLeft { exception -> AdbException(cause = exception) }
                .bind()
        }

    fun runWithDefaultContext(command: String): Either<AdbException, String> =
        either {
            processExecutor.run(command)
                .mapLeft { exception -> AdbException(cause = exception) }
                .bind()
        }
}