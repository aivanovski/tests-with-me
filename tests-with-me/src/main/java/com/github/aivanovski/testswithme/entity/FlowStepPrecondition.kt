package com.github.aivanovski.testswithme.entity

import kotlinx.serialization.Serializable

@Serializable
data class FlowStepPrecondition(
    val type: ConditionType,
    val element: UiElementSelector
)