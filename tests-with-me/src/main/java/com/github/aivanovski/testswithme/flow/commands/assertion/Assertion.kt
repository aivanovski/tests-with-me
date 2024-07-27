package com.github.aivanovski.testswithme.flow.commands.assertion

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.entity.exception.AssertionException

interface Assertion {

    fun describe(): String
    fun assert(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<AssertionException, Unit>
}