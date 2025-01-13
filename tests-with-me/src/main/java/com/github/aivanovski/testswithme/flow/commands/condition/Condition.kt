package com.github.aivanovski.testswithme.flow.commands.condition

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.flow.error.FlowError

interface Condition {
    fun describe(): String
    fun isSatisfied(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<FlowError, Boolean>
}