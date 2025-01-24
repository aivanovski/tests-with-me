package com.github.aivanovski.testswithme.web.api.request

import com.github.aivanovski.testswithme.web.api.dto.DeviceRequestItemDto
import kotlinx.serialization.Serializable

@Serializable
data class PostFlowRunRequest(
    val flowId: String,
    val durationInMillis: Long,
    val isSuccess: Boolean,
    val result: String,
    val appVersionName: String,
    val appVersionCode: String,
    val reportBase64Content: String,
    val device: DeviceRequestItemDto
)