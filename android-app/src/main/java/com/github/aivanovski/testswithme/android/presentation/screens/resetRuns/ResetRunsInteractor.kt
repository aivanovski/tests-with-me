package com.github.aivanovski.testswithme.android.presentation.screens.resetRuns

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.domain.VersionParser
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.extensions.filterRemoteOnly
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsData
import com.github.aivanovski.testswithme.web.api.response.ResetFlowRunsResponse

class ResetRunsInteractor(
    private val projectRepository: ProjectRepository,
    private val flowRepository: FlowRepository,
    private val flowRunRepository: FlowRunRepository,
    private val versionParser: VersionParser
) {

    suspend fun loadData(projectUid: String): Either<AppException, ResetRunsData> =
        either {
            val project = projectRepository.getProjectByUid(projectUid).bind()

            val flows = flowRepository.getFlowsByProjectUid(projectUid).bind()
                .filterRemoteOnly()

            val allFlowUids = flows
                .map { flow -> flow.uid }
                .toSet()

            val runs = flowRunRepository.getRuns().bind()
                .filter { run -> run.flowUid in allFlowUids }

            val versions = getVersionsFromRuns(runs)

            ResetRunsData(
                project = project,
                versionNames = versions.map { version -> version.name },
                flows = flows
            )
        }

    suspend fun resetRuns(
        projectUid: String,
        versionName: String
    ): Either<AppException, ResetFlowRunsResponse> =
        flowRunRepository.resetRuns(projectUid, versionName)

    private fun getVersionsFromRuns(runs: List<FlowRun>): List<AppVersion> {
        return runs
            .map { run ->
                versionParser.parseVersions(
                    versionName = run.appVersionName,
                    versionCode = run.appVersionCode
                )
            }
            .distinct()
            .sortedByDescending { version -> version.code }
    }
}