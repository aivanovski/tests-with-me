package com.github.aivanovski.testswithme.cli.data.adb.command

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.adb.AdbExecutor
import com.github.aivanovski.testswithme.cli.entity.exception.AdbException
import com.github.aivanovski.testswithme.utils.StringUtils.SEMICOLON
import com.github.aivanovski.testswithme.utils.StringUtils.SPACE

class CheckApplicationInstalledCommand(
    private val packageName: String
) : AdbCommand<Boolean> {

    override fun execute(executor: AdbExecutor): Either<AdbException, Boolean> =
        either {
            val dump = executor.run("adb shell dumpsys package")
                .bind()
                .split("\n")

            val categoryToTableMap = HashMap<String, MutableList<String>>()
            var category: String? = null
            for (line in dump) {
                if (!line.startsWith(SPACE) && line.trim().endsWith(SEMICOLON)) {
                    category = line.trim()
                } else {
                    if (category == null) {
                        continue
                    }

                    val attributes = categoryToTableMap[category] ?: mutableListOf<String>()

                    attributes.add(line)


                    categoryToTableMap[category] = attributes
                }
            }

            val activityTable = categoryToTableMap[ACTIVITY_RESOLVER_TABLE] ?: emptyList()

            isInstalled(packageName, activityTable)
        }

    private fun isInstalled(
        packageName: String,
        table: List<String>
    ): Boolean {
        return table.any { line -> line.contains(packageName) }
    }

    companion object {
        private const val ACTIVITY_RESOLVER_TABLE = "Activity Resolver Table:"
    }
}
