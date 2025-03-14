package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.domain.fomatters.RegularNodeFormatter
import com.github.aivanovski.testswithme.extensions.format
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class PrintUiTree : ExecutableStepCommand<Unit> {

    private val nodeFormatter = RegularNodeFormatter()

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> =
        either {
            val uiRoot = context.driver.getUiTree().bind()

            context.logger.debug(uiRoot.format(nodeFormatter))
        }
}