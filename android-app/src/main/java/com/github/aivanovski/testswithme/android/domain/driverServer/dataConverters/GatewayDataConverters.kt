package com.github.aivanovski.testswithme.android.domain.driverServer.dataConverters

import com.github.aivanovski.testswithme.android.driverServerApi.dto.ExecutionResultDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.FlowDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.StepDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.StepResultDto
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.entity.StepResult
import com.github.aivanovski.testswithme.extensions.dumpToString
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.utils.StringUtils

fun JobEntry.toDto(): JobDto =
    JobDto(
        id = uid,
        status = status.toDto(),
        executionResult = executionResult.toDto()
    )

fun JobStatus.toDto(): JobStatusDto =
    when (this) {
        JobStatus.PENDING -> JobStatusDto.PENDING
        JobStatus.RUNNING -> JobStatusDto.RUNNING
        JobStatus.CANCELLED -> JobStatusDto.CANCELLED
        JobStatus.FINISHED -> JobStatusDto.FINISHED
    }

fun ExecutionResult.toDto(): ExecutionResultDto =
    when (this) {
        ExecutionResult.NONE -> ExecutionResultDto.NONE
        ExecutionResult.FAILED -> ExecutionResultDto.FAILED
        ExecutionResult.SUCCESS -> ExecutionResultDto.SUCCESS
    }

fun FlowWithSteps.toDto(
    jsonSerializer: JsonSerializer,
    stepUidToStepRunMap: Map<String, LocalStepRun>
): FlowDto {
    val steps = steps.map { step ->
        val stepRun = stepUidToStepRunMap[step.uid]

        val stepResult = stepRun?.result?.let { resultText ->
            jsonSerializer.deserialize<StepResult>(resultText)
        }
            ?.unwrap()

        StepDto(
            index = step.index,
            result = stepResult?.toDto()
        )
    }

    return FlowDto(
        name = entry.name,
        steps = steps
    )
}

fun StepResult.toDto(): StepResultDto {
    return StepResultDto(
        isSuccess = isSuccess,
        result = result,
        errorMessage = error?.formatReport() ?: emptyList()
    )
}

private fun FlowError.formatReport(): List<String> {
    return when (this) {
        is FlowError.AssertionError -> {
            listOf(
                cause,
                StringUtils.EMPTY,
                "UI Tree:",
                uiRoot.dumpToString()
            )
        }

        is FlowError.FailedToFindUiNodeError -> {
            mutableListOf<String>()
                .apply {
                    add(cause)

                    if (uiRoot != null) {
                        add(StringUtils.EMPTY)
                        add("UI Tree:")
                        add(uiRoot?.dumpToString() ?: StringUtils.EMPTY)
                    }
                }
        }

        else -> {
            listOf(cause)
        }
    }
}