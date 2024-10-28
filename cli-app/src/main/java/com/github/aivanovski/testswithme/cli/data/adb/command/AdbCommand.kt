package com.github.aivanovski.testswithme.cli.data.adb.command

import arrow.core.Either
import com.github.aivanovski.testswithme.cli.data.adb.AdbExecutor
import com.github.aivanovski.testswithme.cli.entity.exception.AdbException

interface AdbCommand<Result> {

    fun execute(executor: AdbExecutor): Either<AdbException, Result>
}