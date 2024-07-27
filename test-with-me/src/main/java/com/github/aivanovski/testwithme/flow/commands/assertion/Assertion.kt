package com.github.aivanovski.testwithme.flow.commands.assertion

import arrow.core.Either
import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.entity.UiNode
import com.github.aivanovski.testwithme.entity.exception.AssertionException

interface Assertion {

    fun describe(): String
    fun assert(
        uiRoot: UiNode<*>,
        elements: List<UiElementSelector>
    ): Either<AssertionException, Unit>
}