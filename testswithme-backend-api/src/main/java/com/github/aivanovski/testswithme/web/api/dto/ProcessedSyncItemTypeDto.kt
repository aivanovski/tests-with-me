package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
enum class ProcessedSyncItemTypeDto {
    INSERT_GROUP,
    INSERT_FLOW,
    UPDATE_FLOW
}