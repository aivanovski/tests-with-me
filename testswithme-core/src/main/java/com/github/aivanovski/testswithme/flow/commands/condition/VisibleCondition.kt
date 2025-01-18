package com.github.aivanovski.testswithme.flow.commands.condition

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.hasElement
import com.github.aivanovski.testswithme.flow.error.FlowError

class VisibleCondition : Condition {

    override fun describe(): String = "is visible"

    override fun isSatisfied(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<FlowError, Boolean> =
        either {
            val unresolved = elements.filter { element -> !uiRoot.hasElement(element) }

            unresolved.isEmpty()
        }
}