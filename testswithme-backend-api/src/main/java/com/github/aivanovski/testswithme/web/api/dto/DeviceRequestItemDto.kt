package com.github.aivanovski.testswithme.web.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceRequestItemDto(
    val id: String?,
    val name: String?,
    val sdkVersion: String?
)