package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UiBoundsDto(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)