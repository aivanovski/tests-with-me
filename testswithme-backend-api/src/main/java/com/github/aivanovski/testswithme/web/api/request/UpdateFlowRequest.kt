package com.github.aivanovski.testswithme.web.api.request

import com.github.aivanovski.testswithme.web.api.dto.EntityReferenceDto
import kotlinx.serialization.Serializable

@Serializable
data class UpdateFlowRequest(
    val parent: EntityReferenceDto?,
    val base64Content: String?
)