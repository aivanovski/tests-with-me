package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class PressKey(
    private val key: KeyCode
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        val name = when (key) {
            KeyCode.Back -> "Back"
            KeyCode.Home -> "Home"
        }

        return "Press key: %s".format(name)
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowExecutionException, Unit> {
        return context.driver.pressKey(key)
    }
}