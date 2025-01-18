package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class StepResultDto(
    val isSuccess: Boolean,
    val result: String?,
    val errorMessage: List<String>
)