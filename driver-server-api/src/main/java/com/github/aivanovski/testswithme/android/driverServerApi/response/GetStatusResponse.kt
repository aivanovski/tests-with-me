package com.github.aivanovski.testswithme.android.driverServerApi.response

import kotlinx.serialization.Serializable

@Serializable
data class GetStatusResponse(
    val driverStatus: DriverStatus,
    val jobs: List<JobDto>
)