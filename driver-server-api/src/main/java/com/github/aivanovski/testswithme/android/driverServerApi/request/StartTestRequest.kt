package com.github.aivanovski.testswithme.android.driverServerApi.request

import com.github.aivanovski.testswithme.android.driverServerApi.dto.Sha256HashDto
import kotlinx.serialization.Serializable

@Serializable
data class StartTestRequest(
    val name: String,
    val base64Content: String,
    val contentHash: Sha256HashDto
)