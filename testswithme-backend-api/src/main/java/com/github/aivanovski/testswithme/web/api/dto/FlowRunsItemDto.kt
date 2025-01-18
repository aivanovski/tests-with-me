package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlowRunsItemDto(
    val id: String,
    val flowId: String,
    val userId: String,
    val finishedAt: String,
    val finishedAtTimestamp: Long,
    val durationInMillis: Long,
    val isSuccess: Boolean,
    val appVersionName: String,
    val appVersionCode: String,
    val isExpired: Boolean,
    val result: String
)