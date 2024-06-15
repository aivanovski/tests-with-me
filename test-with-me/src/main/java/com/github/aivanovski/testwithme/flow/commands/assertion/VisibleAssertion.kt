package com.github.aivanovski.testwithme.flow.commands.assertion

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.UiNode
import com.github.aivanovski.testwithme.extensions.hasElement
import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.entity.exception.AssertionException
import com.github.aivanovski.testwithme.extensions.toReadableFormat

class VisibleAssertion : Assertion {

    override fun describe(): String = "is visible"

    override fun assert(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<AssertionException, Unit> {
        val unresolvedElements = elements.filter { element -> !uiRoot.hasElement(element) }

        return if (unresolvedElements.isEmpty()) {
            Either.Right(Unit)
        } else {
            Either.Left(
                AssertionException(
                    String.format(
                        "Elements should be visible: %s",
                        unresolvedElements.toReadableFormat()
                    )
                )
            )
        }
    }
}