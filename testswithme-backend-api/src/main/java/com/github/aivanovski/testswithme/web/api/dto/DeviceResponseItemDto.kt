package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceResponseItemDto(
    val id: String,
    val name: String?,
    val sdkVersion: Int?
)