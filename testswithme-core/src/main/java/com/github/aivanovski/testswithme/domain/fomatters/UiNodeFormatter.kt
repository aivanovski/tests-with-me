package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.entity.UiNode

interface UiNodeFormatter {
    fun format(uiNode: UiNode<*>): String
}