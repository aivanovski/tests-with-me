package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScreenStateDto(
    val width: Int,
    val height: Int,
    val uiTree: UiNodeDto
)