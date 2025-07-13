package com.github.aivanovski.testswithme.web.api.request

import com.github.aivanovski.testswithme.web.api.dto.EntityReferenceDto
import kotlinx.serialization.Serializable

@Serializable
data class UpdateGroupRequest(
    val parent: EntityReferenceDto?,
    val name: String?
)