package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

interface ExecutableStepCommand<out Result : Any> : StepCommand {
    suspend fun <NodeType> execute(context: ExecutionContext<NodeType>): Either<FlowError, Result>
}