package com.github.aivanovski.testswithme.android.gatewayServerApi.response

import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.JobDto
import kotlinx.serialization.Serializable

@Serializable
data class GetStatusResponse(
    val driverStatus: DriverStatusDto,
    val jobs: List<JobDto>
)