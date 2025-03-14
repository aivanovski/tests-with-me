package com.github.aivanovski.testswithme.android.domain.gatewayServer.controllers

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testswithme.android.domain.gatewayServer.dataConverters.toDto
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.testswithme.android.entity.exception.GatewayException
import com.github.aivanovski.testswithme.android.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.android.gatewayServerApi.GatewayEndpoints.Params.ID
import com.github.aivanovski.testswithme.android.gatewayServerApi.response.GetJobResponse
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.domain.fomatters.RegularNodeFormatter

class JobController(
    private val jobRepository: JobRepository,
    private val flowRepository: FlowRepository,
    private val stepRunRepository: StepRunRepository,
    private val jsonSerializer: JsonSerializer
) {

    suspend fun getJob(jobUid: String): Either<GatewayException, GetJobResponse> =
        either {
            if (jobUid.isBlank()) {
                raise(InvalidParameterException(ID))
            }

            val data = getData(jobUid)
                .mapLeft { exception -> GatewayException(cause = exception) }
                .bind()

            val stepUidToStepRunMap = data.stepRuns
                .associateBy { stepRun -> stepRun.stepUid }

            GetJobResponse(
                job = data.job.toDto(),
                flow = data.flow.toDto(
                    jsonSerializer = jsonSerializer,
                    uiNodeFormatter = RegularNodeFormatter(),
                    stepUidToStepRunMap = stepUidToStepRunMap
                )
            )
        }

    private suspend fun getData(jobUid: String): Either<AppException, JobData> =
        either {
            val jobs = jobRepository.getAll()
            val historyJobs = jobRepository.getAllHistory()

            val job = jobs
                .firstOrNull { job -> job.uid == jobUid }
                ?: historyJobs.firstOrNull { job -> job.uid == jobUid }
                ?: raise(
                    FailedToFindEntityException(
                        entityName = JobEntry::class.java.simpleName,
                        fieldName = "uid",
                        fieldValue = jobUid
                    )
                )

            val flow = flowRepository.getFlowByUid(job.flowUid).bind()
            val stepRuns = stepRunRepository.getByJobUid(job.uid).bind()

            JobData(
                job = job,
                flow = flow,
                stepRuns = stepRuns
            )
        }

    private data class JobData(
        val job: JobEntry,
        val flow: FlowWithSteps,
        val stepRuns: List<LocalStepRun>
    )
}