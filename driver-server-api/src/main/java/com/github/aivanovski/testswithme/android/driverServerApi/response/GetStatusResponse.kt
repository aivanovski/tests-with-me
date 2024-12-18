package com.github.aivanovski.testswithme.android.driverServerApi.response

import com.github.aivanovski.testswithme.android.driverServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobDto
import kotlinx.serialization.Serializable

@Serializable
data class GetStatusResponse(
    val driverStatus: DriverStatusDto,
    val jobs: List<JobDto>
)