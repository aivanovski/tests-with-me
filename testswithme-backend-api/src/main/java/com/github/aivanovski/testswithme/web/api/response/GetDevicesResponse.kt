package com.github.aivanovski.testswithme.web.api.response

import com.github.aivanovski.testswithme.web.api.dto.DeviceResponseItemDto
import kotlinx.serialization.Serializable

@Serializable
data class GetDevicesResponse(
    val devices: List<DeviceResponseItemDto>
)