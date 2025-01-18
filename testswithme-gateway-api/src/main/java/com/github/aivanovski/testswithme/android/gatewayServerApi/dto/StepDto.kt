package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class StepDto(
    val index: Int,
    val result: StepResultDto?
)