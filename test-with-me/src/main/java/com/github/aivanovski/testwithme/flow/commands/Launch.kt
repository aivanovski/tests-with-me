package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext

class Launch(
    private val packageName: String
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "Launch app: package name = %s".format(packageName)
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowExecutionException, Unit> {
        return context.driver.launchApp(packageName)
    }
}