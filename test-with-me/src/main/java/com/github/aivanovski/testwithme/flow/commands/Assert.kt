package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.entity.exception.FailedToFindNodeException
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.extensions.findNode
import com.github.aivanovski.testwithme.extensions.matches
import com.github.aivanovski.testwithme.extensions.toReadableFormat
import com.github.aivanovski.testwithme.flow.commands.assertion.Assertion
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext

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
    ): Either<FlowExecutionException, Unit> =
        either {
            val uiRoot = context.driver.getUiTree().bind()

            val nodeToLookup = if (parent == null) {
                uiRoot
            } else {
                val parentNode = uiRoot.findNode { node -> node.matches(parent) }
                    ?: raise(FailedToFindNodeException(parent))

                parentNode
            }

            assertion.assert(nodeToLookup, elements).bind()
        }
}