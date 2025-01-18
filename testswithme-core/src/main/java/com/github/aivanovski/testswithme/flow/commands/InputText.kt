package com.github.aivanovski.testswithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.findNode
import com.github.aivanovski.testswithme.extensions.findParentNode
import com.github.aivanovski.testswithme.extensions.matches
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.error.FlowError.FailedToFindUiNodeError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext

class InputText(
    private val data: FlowStep.InputText
) : ExecutableStepCommand<Unit> {

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowError, Unit> =
        either {
            val uiRoot = context.driver.getUiTree().bind()

            val targetNode = findTargetNode(uiRoot).bind()

            if (!targetNode.matches(FOCUSABLE_ELEMENT)) {
                raise(FailedToFindUiNodeError(FOCUSABLE_ELEMENT, uiRoot.toSerializableTree()))
            }

            if (targetNode.entity.isFocused != true) {
                context.driver.tapOn(targetNode).bind()
            }

            context.driver.inputText(data.text, targetNode).bind()
        }

    private fun <NodeType> findTargetNode(
        uiRoot: UiNode<NodeType>
    ): Either<FlowError, UiNode<NodeType>> =
        either {
            val element = data.element

            if (element != null) {
                val node = uiRoot.findNode { node -> node.matches(element) }
                    ?: raise(FailedToFindUiNodeError(element, uiRoot.toSerializableTree()))

                val targetNode = when {
                    !node.matches(EDITABLE_ELEMENT) -> {
                        val editableNode = uiRoot.findParentNode(
                            startNode = node,
                            parentSelector = EDITABLE_ELEMENT
                        ) ?: raise(
                            FailedToFindUiNodeError(
                                EDITABLE_ELEMENT,
                                uiRoot.toSerializableTree()
                            )
                        )

                        editableNode
                    }

                    else -> node
                }

                targetNode
            } else {
                uiRoot.findNode { node -> node.matches(FOCUSED_ELEMENT) }
                    ?: raise(FailedToFindUiNodeError(FOCUSED_ELEMENT, uiRoot.toSerializableTree()))
            }
        }

    companion object {
        private val FOCUSABLE_ELEMENT = UiElementSelector.isFocusable(true)
        private val FOCUSED_ELEMENT = UiElementSelector.isFocused(true)
        private val EDITABLE_ELEMENT = UiElementSelector.isEditable(true)
    }
}