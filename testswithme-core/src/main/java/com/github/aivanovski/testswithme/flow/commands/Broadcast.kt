package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class Broadcast(
    private val data: FlowStep.SendBroadcast
) : ExecutableStepCommand<Unit> {

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> {
        return context.driver.sendBroadcast(
            packageName = data.packageName,
            action = data.action,
            data = data.data
        )
    }
}