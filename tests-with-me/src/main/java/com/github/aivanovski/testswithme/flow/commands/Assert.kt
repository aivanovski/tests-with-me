package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.extensions.findNode
import com.github.aivanovski.testswithme.extensions.matches
import com.github.aivanovski.testswithme.extensions.toReadableFormat
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.flow.commands.assertion.Assertion
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.error.FlowError.FailedToFindUiNodeError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class Assert(
    private val parent: UiElementSelector?,
    private val elements: List<UiElementSelector>,
    private val assertion: Assertion
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return when {
            parent != null -> {
                String.format(
                    "Assert %s: inside [%s] -> %s",
                    assertion.describe(),
                    parent.toReadableFormat(),
                    elements.toReadableFormat()
                )
            }

            else -> "Assert %s: %s".format(assertion.describe(), elements.toReadableFormat())
        }
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> =
        either {
            val uiRoot = context.driver.getUiTree().bind()

            val nodeToLookup = if (parent == null) {
                uiRoot
            } else {
                val parentNode = uiRoot.findNode { node -> node.matches(parent) }
                    ?: raise(FailedToFindUiNodeError(parent, uiRoot.toSerializableTree()))

                parentNode
            }

            assertion.assert(nodeToLookup, elements).bind()
        }
}