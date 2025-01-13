package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.PreconditionedResult
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.flow.commands.condition.Condition
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class Precondition(
    private val condition: Condition,
    private val elements: List<UiElementSelector>,
    val command: StepCommand
) : PreconditionedStepCommand<Any> {

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, PreconditionedResult> =
        either {
            val uiRoot = context.driver.getUiTree().bind()
            val isSatisfied = condition.isSatisfied(uiRoot, elements).bind()

            val commandResult = if (isSatisfied && command is ExecutableStepCommand<Any>) {
                command.execute(context).bind()
            } else {
                null
            }

            PreconditionedResult(
                isSatisfied = isSatisfied,
                result = commandResult
            )
        }
}