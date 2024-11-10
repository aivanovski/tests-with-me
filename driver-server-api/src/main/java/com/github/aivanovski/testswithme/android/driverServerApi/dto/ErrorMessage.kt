package com.github.aivanovski.testswithme.android.driverServerApi.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val base64Message: String
)