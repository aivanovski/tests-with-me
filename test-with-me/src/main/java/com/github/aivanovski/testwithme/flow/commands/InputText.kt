package com.github.aivanovski.testwithme.flow.commands

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.extensions.findNode
import com.github.aivanovski.testwithme.extensions.matches
import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.entity.exception.FailedToFindNodeException
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.extensions.toReadableFormat
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext

class InputText(
    private val text: String,
    private val element: UiElementSelector? = null
) : ExecutableStepCommand<Unit> {

    override fun describe(): String {
        return if (element != null) {
            "Input text: [%s] into %s".format(text, element.toReadableFormat())
        } else {
            "Input text: [%s]".format(text)
        }
    }

    override suspend fun <NodeType> execute(
        context: ExecutionContext<NodeType>
    ): Either<FlowExecutionException, Unit> = either {
        val uiRoot = context.driver.getUiTree().bind()
        val selector = element ?: FOCUSED_ELEMENT

        val targetNode = uiRoot.findNode { node -> node.matches(selector) }
            ?: raise(FailedToFindNodeException(selector))

        context.driver.inputText(text, targetNode).bind()
    }

    companion object {
        private val FOCUSED_ELEMENT = UiElementSelector.isFocused(true)
    }
}