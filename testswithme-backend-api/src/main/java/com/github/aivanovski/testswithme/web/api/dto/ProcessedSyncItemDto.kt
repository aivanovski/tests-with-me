package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProcessedSyncItemDto(
    val path: String,
    val entityId: String?,
    val type: ProcessedSyncItemTypeDto,
    val isSuccess: Boolean
)