package com.github.aivanovski.testswithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class FlowRunsItemDto(
    val uid: String,
    val flowUid: String,
    val userUid: String,
    val finishedAt: String,
    val finishedAtTimestamp: Long,
    val durationInMillis: Long,
    val isSuccess: Boolean,
    val appVersionName: String,
    val appVersionCode: String
)