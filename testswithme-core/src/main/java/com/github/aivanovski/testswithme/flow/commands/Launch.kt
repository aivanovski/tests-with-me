package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class Launch(
    private val packageName: String
) : ExecutableStepCommand<Unit> {

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> {
        return context.driver.launchApp(packageName)
    }
}