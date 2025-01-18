package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class JobDto(
    val id: String,
    val status: JobStatusDto,
    val executionResult: ExecutionResultDto
)