package com.github.aivanovski.testswithme.android.gatewayServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class UiNodeDto(
    val entity: UiEntityDto,
    val nodes: MutableList<UiNodeDto>
)