package com.github.aivanovski.testswithme.entity

import arrow.core.Either
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.entity.exception.TestsWithMeException
import com.github.aivanovski.testswithme.flow.error.FlowError
import kotlinx.serialization.Serializable

@Serializable
data class StepResult(
    val isSuccess: Boolean,
    val result: String?,
    val error: FlowError?
) {

    companion object {

        fun deserialize(
            jsonSerializer: JsonSerializer,
            content: String
        ): Either<TestsWithMeException, StepResult?> =
            jsonSerializer.deserialize<StepResult>(content)

        fun serialize(
            jsonSerializer: JsonSerializer,
            data: StepResult
        ): String = jsonSerializer.serialize(data)
    }
}