package com.github.aivanovski.testswithme.web.api.request

import com.github.aivanovski.testswithme.web.api.EntityReference
import kotlinx.serialization.Serializable

@Serializable
data class UpdateGroupRequest(
    val parent: EntityReference?,
    val name: String?
)