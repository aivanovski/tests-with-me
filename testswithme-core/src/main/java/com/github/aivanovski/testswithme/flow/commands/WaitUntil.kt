package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.ConditionType
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.toMilliseconds
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.flow.commands.condition.NotVisibleCondition
import com.github.aivanovski.testswithme.flow.commands.condition.VisibleCondition
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.error.FlowError.FailedToFindUiNodeError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext
import kotlinx.coroutines.delay

class WaitUntil(
    private val data: FlowStep.WaitUntil
) : ExecutableStepCommand<Unit> {

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> =
        either {
            val startTime = System.currentTimeMillis()

            var uiRoot: UiNode<NodeType>? = null

            val delayScale = context.environment.getDelayScaleFactor()
            val timeoutInMs = data.timeout.toMilliseconds() * delayScale
            val stepInMs = data.step.toMilliseconds() * delayScale

            val element = data.element
            val condition = when (data.conditionType) {
                ConditionType.VISIBLE -> VisibleCondition()
                ConditionType.NOT_VISIBLE -> NotVisibleCondition()
            }

            while ((System.currentTimeMillis() - startTime) <= timeoutInMs) {
                delay(stepInMs)

                uiRoot = context.driver.getUiTree().bind()

                val isSatisfied = condition.isSatisfied(uiRoot, listOf(element)).bind()
                if (isSatisfied) {
                    return@either
                }
            }

            raise(FailedToFindUiNodeError(element, uiRoot?.toSerializableTree()))
        }
}