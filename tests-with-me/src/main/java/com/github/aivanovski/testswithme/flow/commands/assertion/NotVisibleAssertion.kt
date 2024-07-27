package com.github.aivanovski.testswithme.flow.commands.assertion

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.entity.exception.AssertionException
import com.github.aivanovski.testswithme.extensions.hasElement
import com.github.aivanovski.testswithme.extensions.toReadableFormat

class NotVisibleAssertion : Assertion {

    override fun describe(): String = "is not visible"

    override fun assert(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<AssertionException, Unit> {
        val unresolvedElements = elements.filter { element -> uiRoot.hasElement(element) }

        return if (unresolvedElements.isEmpty()) {
            Either.Right(Unit)
        } else {
            Either.Left(
                AssertionException(
                    String.format(
                        "Elements should not be visible: %s",
                        unresolvedElements.toReadableFormat()
                    )
                )
            )
        }
    }
}