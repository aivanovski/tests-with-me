package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

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