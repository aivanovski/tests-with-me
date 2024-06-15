package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testwithme.flow.driver.Driver
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException

interface ExecutableStepCommand<out Result : Any> : StepCommand {
    suspend fun <NodeType> execute(
        driver: Driver<NodeType>
    ): Either<FlowExecutionException, Result>
}