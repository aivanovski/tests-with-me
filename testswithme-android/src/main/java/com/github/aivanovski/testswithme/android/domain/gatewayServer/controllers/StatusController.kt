package com.github.aivanovski.testswithme.android.domain.gatewayServer.controllers

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.domain.flow.UiTreeCollector
import com.github.aivanovski.testswithme.android.domain.gatewayServer.dataConverters.toDto
import com.github.aivanovski.testswithme.android.domain.flow.model.DriverServiceState
import com.github.aivanovski.testswithme.android.domain.flow.model.FlowRunnerState
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.exception.GatewayException
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.UiNodeDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.extensions.transformNode

class StatusController(
    private val runnerManager: FlowRunnerManager,
    private val jobRepository: JobRepository,
    private val uiTreeCollector: UiTreeCollector
) {

    fun getStatus(): Either<GatewayException, GetStatusResponse> =
        either {

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

            val driverState = runnerManager.getDriverState()
            val driverStatus = when (driverState) {
                DriverServiceState.RUNNING -> DriverStatusDto.RUNNING
                else -> DriverStatusDto.STOPPED
            }

            val runnerState = runnerManager.getRunnerState()

            val uiTree = if (runnerState == FlowRunnerState.IDLE) {
                runnerManager.getUiTree() ?: uiTreeCollector.getUiTree()
            } else {
                runnerManager.setCollectUiTreeFlag()
                uiTreeCollector.getUiTree()
            }

            val uiTreeDto = uiTree?.transformNode(
                onNewNode = { uiNode ->
                    UiNodeDto(
                        entity = uiNode.entity.toDto(),
                        nodes = mutableListOf()
                    )
                },
                onNewChild = { parent, child ->
                    parent.nodes.add(child)
                }
            )

            GetStatusResponse(
                driverStatus = driverStatus,
                jobs = filteredJobs.map { job -> job.toDto() },
                uiTree = uiTreeDto
            )
        }
}