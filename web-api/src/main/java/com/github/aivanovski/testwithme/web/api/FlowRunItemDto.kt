package com.github.aivanovski.testwithme.web.api

import kotlinx.serialization.Serializable

@Serializable
data class FlowRunItemDto(
    val uid: String,
    val flowUid: String,
    val userUid: String,
    val finishedAt: String,
    val finishedAtTimestamp: Long,
    val durationInMillis: Long,
    val isSuccess: Boolean,
    val appVersionName: String,
    val appVersionCode: String,
    val reportBase64Content: String
)