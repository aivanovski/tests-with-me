package com.github.aivanovski.testswithme.android.driverServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class StepDto(
    val index: Int,
    val result: String?
)