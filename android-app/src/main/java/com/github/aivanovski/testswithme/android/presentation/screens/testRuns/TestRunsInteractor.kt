package com.github.aivanovski.testswithme.android.presentation.screens.testRuns

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testswithme.android.domain.usecases.IsUserLoggedInUseCase
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TestRunsInteractor(
    private val projectRepository: ProjectRepository,
    private val flowRepository: FlowRepository,
    private val jobRepository: JobRepository,
    private val stepRunRepository: StepRunRepository,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
) {

    suspend fun loadData(): Either<AppException, TestRunsData> =
        withContext(Dispatchers.IO) {
            either {
                val projects = if (isUserLoggedInUseCase.isLoggedIn()) {
                    projectRepository.getProjects().bind()
                } else {
                    emptyList()
                }

                val jobHistory = jobRepository.getAllHistory()
                val steps = stepRunRepository.getAll()

                val flowWithSteps = mutableListOf<FlowWithSteps>()

                for (job in jobHistory) {
                    val flow = flowRepository.getCachedFlowByUid(job.flowUid).bind()
                    flowWithSteps.add(flow)
                }

                TestRunsData(
                    allProjects = projects,
                    allFlows = flowWithSteps,
                    jobHistory = jobHistory,
                    localRuns = steps
                )
            }
        }

    fun isLoggedIn(): Boolean = isUserLoggedInUseCase.isLoggedIn()
}