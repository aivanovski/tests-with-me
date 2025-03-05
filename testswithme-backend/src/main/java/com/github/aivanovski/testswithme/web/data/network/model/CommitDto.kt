package com.github.aivanovski.testswithme.web.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CommitDto(
    val sha: String
)