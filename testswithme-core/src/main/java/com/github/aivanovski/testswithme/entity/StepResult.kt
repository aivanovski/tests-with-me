package com.github.aivanovski.testswithme.entity

import com.github.aivanovski.testswithme.flow.error.FlowError
import kotlinx.serialization.Serializable

@Serializable
data class StepResult(
    val isSuccess: Boolean,
    val result: String?,
    val error: FlowError?
)