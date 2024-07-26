package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.extensions.hasElement
import com.github.aivanovski.testwithme.entity.Duration
import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.entity.exception.FailedToFindNodeException
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.extensions.toMilliseconds
import com.github.aivanovski.testwithme.extensions.toReadableFormat
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext
import kotlinx.coroutines.delay

class WaitUntil(
    private val element: UiElementSelector,
    private val step: Duration,
    private val timeout: Duration
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return String.format(
            "Wait for element: %s, timeout = %s, step = %s",
            element.toReadableFormat(),
            timeout.toReadableFormat(),
            step.toReadableFormat()
        )
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowExecutionException, Unit> = either {
        val startTime = System.currentTimeMillis()

        while ((System.currentTimeMillis() - startTime) <= timeout.toMilliseconds()) {
            delay(step.toMilliseconds())

            val uiRoot = context.driver.getUiTree().bind()
            if (uiRoot.hasElement(element)) {
                return@either
            }
        }

        raise(FailedToFindNodeException(element))
    }
}