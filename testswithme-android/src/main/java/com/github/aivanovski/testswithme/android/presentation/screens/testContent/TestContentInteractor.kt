package com.github.aivanovski.testswithme.android.presentation.screens.testContent

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.raise.either
import arrow.core.some
import com.github.aivanovski.testswithme.android.data.file.FileCache
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentData
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentScreenMode
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.extensions.unwrapOrReport
import com.github.aivanovski.testswithme.flow.runner.report.ReportParser
import com.github.aivanovski.testswithme.flow.runner.report.model.ReportItem
import timber.log.Timber

class TestContentInteractor(
    private val flowRepository: FlowRepository,
    private val stepRunRepository: StepRunRepository,
    private val flowRunRepository: FlowRunRepository,
    private val jobRepository: JobRepository,
    private val fileCache: FileCache
) {

    private val reportParser = ReportParser()

    suspend fun loadData(
        flowUid: String,
        mode: TestContentScreenMode
    ): Either<AppException, TestContentData> =
        either {
            when (mode) {
                is TestContentScreenMode.FlowContent -> {
                    TestContentData(
                        flow = getFlow(flowUid).bind(),
                        localRuns = emptyList(),
                        job = null,
                        remoteRun = null,
                        report = null,
                        parsedReport = null
                    )
                }

                else -> loadRunData(flowUid, mode).bind()
            }
        }

    private suspend fun loadRunData(
        flowUid: String,
        mode: TestContentScreenMode
    ): Either<AppException, TestContentData> =
        either {
            val (flowRun, report) = getFlowRunAndReport(mode).bind()

            TestContentData(
                flow = getFlow(flowUid).bind(),
                localRuns = getLocalRuns(flowUid, mode).bind(),
                job = getJob(mode).bind().getOrNull(),
                remoteRun = flowRun,
                report = report,
                parsedReport = parseReport(report).bind()
            )
        }

    private fun getJob(mode: TestContentScreenMode): Either<AppException, Option<JobEntry>> =
        either {
            when (mode) {
                is TestContentScreenMode.LocalRun ->
                    jobRepository.getJobHistoryByUid(mode.jobUid).bind()
                        .some()

                is TestContentScreenMode.RemoteRun -> None

                else -> None
            }
        }

    private fun getFlow(flowUid: String): Either<AppException, FlowWithSteps> =
        flowRepository.getCachedFlowByUid(flowUid)

    private fun getLocalRuns(
        flowUid: String,
        mode: TestContentScreenMode
    ): Either<AppException, List<LocalStepRun>> =
        either {
            when (mode) {
                is TestContentScreenMode.LocalRun -> {
                    val jobUid = mode.jobUid

                    stepRunRepository.getByFlowUid(flowUid).bind()
                        .filter { run -> run.jobUid == jobUid }
                }

                is TestContentScreenMode.RemoteRun -> {
                    val flowRunUid = mode.flowRunUid

                    val jobEntry = jobRepository.getAllHistory()
                        .firstOrNull { job -> job.flowRunUid == flowRunUid }

                    stepRunRepository.getByFlowUid(flowUid).bind()
                        .filter { stepRun ->
                            stepRun.jobUid == jobEntry?.uid
                        }
                }

                else -> emptyList()
            }
        }

    private suspend fun getFlowRunAndReport(
        mode: TestContentScreenMode
    ): Either<AppException, Pair<FlowRunEntry?, String?>> =
        either {
            when (mode) {
                is TestContentScreenMode.LocalRun -> {
                    null to fileCache.get(mode.jobUid).bind()
                }

                is TestContentScreenMode.RemoteRun -> {
                    val runAndReport = flowRunRepository.getRun(mode.flowRunUid).bind()
                    runAndReport.run to runAndReport.report
                }

                else -> null to null
            }
        }

    private fun parseReport(report: String?): Either<AppException, ReportItem.FlowItem?> =
        either {
            if (!report.isNullOrBlank()) {
                val parseResult = reportParser.parse(report)
                if (parseResult.isLeft()) {
                    val error = parseResult.unwrapError()
                    Timber.w("Failed to parse report: $error")
                    Timber.w(error)
                }

                parseResult.getOrNull()
            } else {
                null
            }
        }
}