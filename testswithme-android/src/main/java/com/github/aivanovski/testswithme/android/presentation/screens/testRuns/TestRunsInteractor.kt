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
import com.github.aivanovski.testswithme.android.extensions.filterBySourceType
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsData
import com.github.aivanovski.testswithme.android.utils.combineEitherFlows
import kotlinx.coroutines.flow.Flow

class TestRunsInteractor(
    private val projectRepository: ProjectRepository,
    private val flowRepository: FlowRepository,
    private val jobRepository: JobRepository,
    private val stepRunRepository: StepRunRepository,
    private val authRepository: AuthRepository
) {

    fun isLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun isLoggedInFlow() = authRepository.isLoggedInFlow()

    fun loadData(): Flow<Either<AppException, TestRunsData>> =
        combineEitherFlows(
            projectRepository.getProjectsFlow(),
            flowRepository.getFlowsFlow()
        ) { allProjects, allFlows ->
            either {
                val jobHistory = jobRepository.getAllHistory()

                val filteredHistory = if (authRepository.isUserLoggedIn()) {
                    jobHistory
                } else {
                    val localFlowUids = allFlows
                        .filterBySourceType(SourceType.LOCAL)
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
                    allProjects = allProjects,
                    allFlows = flowWithSteps,
                    jobHistory = filteredHistory,
                    localRuns = steps
                )
            }
        }
}