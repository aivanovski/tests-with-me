package com.github.aivanovski.testswithme.web.api.response

import kotlinx.serialization.Serializable

@Serializable
data class DeleteGroupResponse(
    val isSuccess: Boolean,
    val modifiedGroupIds: List<String>,
    val modifiedFlowIds: List<String>
)