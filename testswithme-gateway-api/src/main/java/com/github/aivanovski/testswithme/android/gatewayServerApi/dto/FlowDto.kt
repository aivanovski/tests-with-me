package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlowDto(
    val name: String,
    val steps: List<StepDto>
)