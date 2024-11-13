package com.github.aivanovski.testswithme.flow.commands.assertion

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.hasElement
import com.github.aivanovski.testswithme.extensions.toReadableFormat
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.flow.error.FlowError.AssertionError

class VisibleAssertion : Assertion {

    override fun describe(): String = "is visible"

    override fun assert(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<AssertionError, Unit> {
        val unresolvedElements = elements.filter { element -> !uiRoot.hasElement(element) }

        return if (unresolvedElements.isEmpty()) {
            Either.Right(Unit)
        } else {
            Either.Left(
                AssertionError(
                    message = String.format(
                        "Elements should be visible: %s",
                        unresolvedElements.toReadableFormat()
                    ),
                    uiRoot = uiRoot.toSerializableTree()
                )
            )
        }
    }
}