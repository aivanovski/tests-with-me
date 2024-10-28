package com.github.aivanovski.testswithme.cli.data.adb

import arrow.core.Either
import com.github.aivanovski.testswithme.cli.data.adb.command.AdbCommand
import com.github.aivanovski.testswithme.cli.data.process.ProcessExecutor
import com.github.aivanovski.testswithme.cli.entity.AdbDevice
import com.github.aivanovski.testswithme.cli.entity.exception.AdbException

class AdbContext(
    processExecutor: ProcessExecutor,
    device: AdbDevice?
) {

    private val executor: AdbExecutor = AdbExecutor(
        processExecutor = processExecutor,
        device = device
    )

    fun <T> execute(command: AdbCommand<T>): Either<AdbException, T> {
        return command.execute(executor)
    }
}