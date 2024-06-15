package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import com.github.aivanovski.testwithme.flow.driver.Driver
import com.github.aivanovski.testwithme.entity.KeyCode
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException

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
        driver: Driver<NodeType>
    ): Either<FlowExecutionException, Unit> {
        return driver.pressKey(key)
    }
}