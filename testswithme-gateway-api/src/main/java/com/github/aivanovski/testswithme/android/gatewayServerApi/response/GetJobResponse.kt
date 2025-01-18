package com.github.aivanovski.testswithme.android.gatewayServerApi.response

import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.FlowDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.JobDto
import kotlinx.serialization.Serializable

@Serializable
data class GetJobResponse(
    val job: JobDto,
    val flow: FlowDto
)