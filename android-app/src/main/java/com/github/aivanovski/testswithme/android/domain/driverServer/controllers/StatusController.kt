package com.github.aivanovski.testswithme.android.domain.driverServer.controllers

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.domain.driverServer.dataConverters.toDto
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.driverServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.android.entity.DriverServiceState
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.exception.GatewayException

class StatusController(
    private val jobRepository: JobRepository
) {

    fun getStatus(): Either<GatewayException, GetStatusResponse> =
        either {
            val driverState = FlowRunnerManager.getDriverState()

            val jobs = jobRepository.getAll()
                .sortedByDescending { job -> job.addedTimestamp }

            val history = jobRepository.getAllHistory()
                .sortedByDescending { job -> job.addedTimestamp }
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
                DriverServiceState.RUNNING -> DriverStatusDto.RUNNING
                else -> DriverStatusDto.STOPPED
            }

            GetStatusResponse(
                driverStatus = driverStatus,
                jobs = filteredJobs.map { job -> job.toDto() }
            )
        }
}