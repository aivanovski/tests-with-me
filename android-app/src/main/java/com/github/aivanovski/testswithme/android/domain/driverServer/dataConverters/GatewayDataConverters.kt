package com.github.aivanovski.testswithme.android.domain.driverServer.dataConverters

import com.github.aivanovski.testswithme.android.driverServerApi.dto.ExecutionResultDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.FlowDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.StepDto
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun

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

fun FlowWithSteps.toDto(stepUidToStepRunMap: Map<String, LocalStepRun>): FlowDto {
    val steps = steps.map { step ->
        val stepRun = stepUidToStepRunMap[step.uid]

        StepDto(
            index = step.index,
            result = stepRun?.result
        )
    }

    return FlowDto(
        name = entry.name,
        steps = steps
    )
}