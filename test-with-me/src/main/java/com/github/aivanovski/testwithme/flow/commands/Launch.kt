package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testwithme.flow.driver.Driver
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException

class Launch(
    private val packageName: String
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Launch app: package name = %s".format(packageName)
    }

    override suspend fun <NodeType> execute(
        driver: Driver<NodeType>
    ): Either<FlowExecutionException, Unit> {
        return driver.launchApp(packageName)
    }
}