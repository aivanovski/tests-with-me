package com.github.aivanovski.testswithme.cli.data.adb.command

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.adb.AdbExecutor
import com.github.aivanovski.testswithme.cli.entity.AdbDevice
import com.github.aivanovski.testswithme.cli.entity.exception.AdbException
import com.github.aivanovski.testswithme.utils.StringUtils

class GetDevicesCommand : AdbCommand<List<AdbDevice>> {

    override fun execute(executor: AdbExecutor): Either<AdbException, List<AdbDevice>> =
        either {
            val result = executor.runWithDefaultContext("adb devices").bind()

            result
                .split(StringUtils.NEW_LINE)
                .map { line -> line.trim() }
                .filter { line -> line.isNotEmpty() && !line.startsWith("List of devices") }
                .mapNotNull { line -> parseDevice(line) }
        }

    private fun parseDevice(line: String): AdbDevice? {
        val values = line.split(StringUtils.TAB)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (values.size != 2) {
            return null
        }

        return AdbDevice(id = values.first())
    }
}