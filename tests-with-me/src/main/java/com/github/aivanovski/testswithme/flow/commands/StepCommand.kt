package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.PreconditionedResult
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

sealed interface StepCommand

interface ExecutableStepCommand<out Result : Any> : StepCommand {
    suspend fun <NodeType> execute(context: ExecutionContext<NodeType>): Either<FlowError, Result>
}

interface PreconditionedStepCommand<out Result : Any> : StepCommand {
    suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, PreconditionedResult>
}

interface CompositeStepCommand : StepCommand {
    fun getCommands(): List<ExecutableStepCommand<Any>>
}