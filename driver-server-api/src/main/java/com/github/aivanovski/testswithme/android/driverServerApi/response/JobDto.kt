package com.github.aivanovski.testswithme.android.driverServerApi.response

import kotlinx.serialization.Serializable

@Serializable
data class JobDto(
    val id: String,
    val status: JobDtoStatus
)