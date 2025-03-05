package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncResultDto(
    val isSuccess: Boolean,
    val startedAt: String,
    val startedAtTimestamp: Long,
    val finishedAt: String,
    val finishedAtTimestamp: Long,
    val processedItems: List<ProcessedSyncItemDto>
)