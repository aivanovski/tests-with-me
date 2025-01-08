package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.Duration
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.hasElement
import com.github.aivanovski.testswithme.extensions.toMilliseconds
import com.github.aivanovski.testswithme.extensions.toReadableFormat
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.error.FlowError.FailedToFindUiNodeError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext
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
    ): Either<FlowError, Unit> =
        either {
            val startTime = System.currentTimeMillis()

            var uiRoot: UiNode<NodeType>? = null

            val delayScale = context.environment.getDelayScaleFactor()
            val timeoutInMs = timeout.toMilliseconds() * delayScale
            val stepInMs = step.toMilliseconds() * delayScale

            while ((System.currentTimeMillis() - startTime) <= timeoutInMs) {
                delay(stepInMs)

                uiRoot = context.driver.getUiTree().bind()
                if (uiRoot.hasElement(element)) {
                    return@either
                }
            }

            raise(FailedToFindUiNodeError(element, uiRoot?.toSerializableTree()))
        }
}