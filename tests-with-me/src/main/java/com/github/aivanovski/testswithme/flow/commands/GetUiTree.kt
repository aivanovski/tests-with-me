package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class GetUiTree<T> : ExecutableStepCommand<UiNode<T>> {

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, UiNode<T>> =
        either {
            context.driver.getUiTree().bind() as UiNode<T>
        }
}