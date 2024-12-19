package com.github.aivanovski.testswithme.android.presentation.screens.testRuns

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsData

class TestRunsInteractor(
    private val projectRepository: ProjectRepository,
    private val flowRepository: FlowRepository,
    private val jobRepository: JobRepository,
    private val stepRunRepository: StepRunRepository,
    private val authRepository: AuthRepository
) {

    fun isLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun isLoggedInFlow() = authRepository.isLoggedInFlow()

    suspend fun loadData(): Either<AppException, TestRunsData> =
        either {
            val projects = projectRepository.getProjects().bind()

            val jobHistory = jobRepository.getAllHistory()

            val filteredHistory = if (authRepository.isUserLoggedIn()) {
                jobHistory
            } else {
                val flows = flowRepository.getFlows().bind()

                val localFlowUids = flows
                    .filter { flow -> flow.sourceType == SourceType.LOCAL }
                    .map { flow -> flow.uid }
                    .toSet()

                jobHistory.filter { history -> history.flowUid in localFlowUids }
            }

            val steps = stepRunRepository.getAll()

            val flowWithSteps = mutableListOf<FlowWithSteps>()

            for (job in filteredHistory) {
                val flow = flowRepository.getCachedFlowByUid(job.flowUid).bind()
                flowWithSteps.add(flow)
            }

            TestRunsData(
                allProjects = projects,
                allFlows = flowWithSteps,
                jobHistory = filteredHistory,
                localRuns = steps
            )
        }
}