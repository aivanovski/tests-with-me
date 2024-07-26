package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext

interface ExecutableStepCommand<out Result : Any> : StepCommand {
    suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowExecutionException, Result>
}