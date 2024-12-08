package com.github.aivanovski.testswithme.android.driverServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class Sha256HashDto(
    val value: String
)