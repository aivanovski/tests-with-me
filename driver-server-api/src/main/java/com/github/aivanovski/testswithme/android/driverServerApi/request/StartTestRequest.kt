package com.github.aivanovski.testswithme.android.driverServerApi.request

import kotlinx.serialization.Serializable

@Serializable
data class StartTestRequest(
    val name: String,
    val base64Content: String
)