package com.github.aivanovski.testswithme.android.domain.driverServer.controllers

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.driverServerApi.response.DriverStatus
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.android.driverServerApi.response.JobDto
import com.github.aivanovski.testswithme.android.driverServerApi.response.JobDtoStatus
import com.github.aivanovski.testswithme.android.entity.DriverServiceState
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException

class StatusController(
    private val jobRepository: JobRepository
) {

    fun getStatus(): Either<AppException, GetStatusResponse> =
        either {
            val driverState = FlowRunnerManager.getDriverState()

            val jobs = jobRepository.getAll()
                .sortedBy { job -> job.addedTimestamp }

            val history = jobRepository.getAllHistory()
                .sortedBy { job -> job.addedTimestamp }
                .take(10)

            val currentJob = jobs.firstOrNull { job ->
                job.status == JobStatus.RUNNING
            }

            val filteredJobs = if (currentJob != null) {
                listOf(currentJob) + history
            } else {
                history
            }

            val driverStatus = when (driverState) {
                DriverServiceState.RUNNING -> DriverStatus.RUNNING
                else -> DriverStatus.STOPPED
            }

            GetStatusResponse(
                driverStatus = driverStatus,
                jobs = filteredJobs.map { job -> job.toDto() }
            )
        }

    private fun JobEntry.toDto(): JobDto =
        JobDto(
            id = uid,
            status = status.toDto()
        )

    private fun JobStatus.toDto(): JobDtoStatus =
        when (this) {
            JobStatus.PENDING -> JobDtoStatus.PENDING
            JobStatus.RUNNING -> JobDtoStatus.RUNNING
            JobStatus.CANCELLED -> JobDtoStatus.CANCELLED
            JobStatus.FINISHED -> JobDtoStatus.FINISHED
        }
}