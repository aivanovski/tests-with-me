package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.extensions.findNode
import com.github.aivanovski.testswithme.extensions.getNodeParents
import com.github.aivanovski.testswithme.extensions.matches
import com.github.aivanovski.testswithme.extensions.toReadableFormat
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.error.FlowError.FailedToFindUiNodeError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class Tap(
    private val element: UiElementSelector,
    private val isLongTap: Boolean
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return "%s on element: %s".format(
            if (isLongTap) "Long Tap" else "Tap",
            element.toReadableFormat()
        )
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> =
        either {
            val uiRoot = context.driver.getUiTree().bind()

            val node = uiRoot.findNode { node -> node.matches(element) }
                ?: raise(FailedToFindUiNodeError(element, uiRoot.toSerializableTree()))

            val nodeSelector = getSelectorForNode()

            val tappableNode = if (!node.matches(nodeSelector)) {
                val parents = uiRoot.getNodeParents(node)

                val clickableParent = parents.lastOrNull { parent -> parent.matches(nodeSelector) }
                    ?: raise(FailedToFindUiNodeError(nodeSelector, uiRoot.toSerializableTree()))

                clickableParent
            } else {
                node
            }

            if (isLongTap) {
                context.driver.longTapOn(tappableNode).bind()
            } else {
                context.driver.tapOn(tappableNode).bind()
            }
        }

    private fun getSelectorForNode(): UiElementSelector {
        return if (isLongTap) {
            LONG_CLICKABLE_ELEMENT
        } else {
            CLICKABLE_ELEMENT
        }
    }

    companion object {
        private val CLICKABLE_ELEMENT = UiElementSelector.isClickable(true)
        private val LONG_CLICKABLE_ELEMENT = UiElementSelector.isLongClickable(true)
    }
}