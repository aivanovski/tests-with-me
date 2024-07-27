package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.entity.UiNode
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext

class GetUiTree<T> : ExecutableStepCommand<UiNode<T>> {

    override fun describe(): String {
        return "Get UI tree"
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowExecutionException, UiNode<T>> =
        either {
            context.driver.getUiTree().bind() as UiNode<T>
        }
}