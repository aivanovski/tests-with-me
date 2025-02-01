package com.github.aivanovski.testswithme.android.domain.gatewayServer.dataConverters

import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.ExecutionResultDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.FlowDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.JobDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.JobStatusDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.Sha256HashDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.StepDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.StepResultDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.UiBoundsDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.UiEntityDto
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.domain.fomatters.UiNodeFormatter
import com.github.aivanovski.testswithme.entity.Bounds
import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.entity.HashType
import com.github.aivanovski.testswithme.entity.StepResult
import com.github.aivanovski.testswithme.entity.UiEntity
import com.github.aivanovski.testswithme.extensions.format
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
    uiNodeFormatter: UiNodeFormatter,
    stepUidToStepRunMap: Map<String, LocalStepRun>
): FlowDto {
    val steps = steps.map { step ->
        val stepRun = stepUidToStepRunMap[step.uid]

        val stepResult = stepRun?.result?.let { resultText ->
            jsonSerializer.deserialize<StepResult>(resultText)
                .getOrNull()
        }

        StepDto(
            index = step.index,
            result = stepResult?.toDto(uiNodeFormatter)
        )
    }

    return FlowDto(
        name = entry.name,
        steps = steps
    )
}

fun StepResult.toDto(
    uiNodeFormatter: UiNodeFormatter
): StepResultDto {
    return StepResultDto(
        isSuccess = isSuccess,
        result = result,
        errorMessage = error?.formatReport(uiNodeFormatter) ?: emptyList()
    )
}

private fun FlowError.formatReport(
    uiNodeFormatter: UiNodeFormatter
): List<String> {
    return when (this) {
        is FlowError.AssertionError -> {
            listOf(
                cause,
                StringUtils.EMPTY,
                "UI Tree:",
                uiRoot.format(uiNodeFormatter)
            )
        }

        is FlowError.FailedToFindUiNodeError -> {
            mutableListOf<String>()
                .apply {
                    add(cause)

                    if (uiRoot != null) {
                        add(StringUtils.EMPTY)
                        add("UI Tree:")
                        add(uiRoot?.format(uiNodeFormatter) ?: StringUtils.EMPTY)
                    }
                }
        }

        else -> {
            listOf(cause)
        }
    }
}

fun Sha256HashDto.convert(): Hash {
    return Hash(
        type = HashType.SHA_256,
        value = value
    )
}

fun UiEntity.toDto(): UiEntityDto =
    UiEntityDto(
        packageName = packageName,
        className = className,
        bounds = bounds?.toDto(),
        text = text,
        contentDescription = contentDescription,
        isEnabled = isEnabled,
        isEditable = isEditable,
        isFocused = isFocused,
        isFocusable = isFocusable,
        isClickable = isClickable,
        isLongClickable = isLongClickable,
        isCheckable = isCheckable,
        isChecked = isChecked
    )

fun Bounds.toDto(): UiBoundsDto =
    UiBoundsDto(
        left = left,
        top = top,
        right = right,
        bottom = bottom
    )