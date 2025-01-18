package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class PressKey(
    private val key: KeyCode
) : ExecutableStepCommand<Unit> {

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> {
        return context.driver.pressKey(key)
    }
}