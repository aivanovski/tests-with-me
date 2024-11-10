package com.github.aivanovski.testswithme.android.driverServerApi.response

import com.github.aivanovski.testswithme.android.driverServerApi.dto.FlowDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobDto
import kotlinx.serialization.Serializable

@Serializable
data class GetJobResponse(
    val job: JobDto,
    val flow: FlowDto
)