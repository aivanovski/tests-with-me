package com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.repository.FlowRepository
import com.github.aivanovski.testwithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testwithme.android.data.repository.GroupRepository
import com.github.aivanovski.testwithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testwithme.android.domain.VersionParser
import com.github.aivanovski.testwithme.android.entity.AppVersion
import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.model.ProjectDashboardData
import com.github.aivanovski.testwithme.android.utils.aggregatePassedFailedAndRemainedFlows
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectDashboardInteractor(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val flowRepository: FlowRepository,
    private val flowRunRepository: FlowRunRepository,
    private val versionParser: VersionParser
) {

    suspend fun loadData(
        projectUid: String,
        versionName: String?
    ): Either<AppException, ProjectDashboardData> =
        withContext(Dispatchers.IO) {
            either {
                val allGroups = groupRepository.getGroupsByProjectUid(projectUid).bind()
                val allFlows = flowRepository.getFlowsByProjectUid(projectUid).bind()

                val allFlowUids = allFlows
                    .map { flow -> flow.uid }
                    .toSet()

                val allRuns = flowRunRepository.getRuns()
                    .bind()
                    .filter { run -> run.flowUid in allFlowUids }

                val versions = getVersionsFromRuns(allRuns)

                val selectedVersion = if (versionName != null) {
                    versions.firstOrNull { version -> version.name == versionName }
                } else {
                    versions.firstOrNull()
                }

                val versionRuns = if (selectedVersion != null) {
                    allRuns.filter { run -> run.appVersionName == selectedVersion.name }
                } else {
                    allRuns
                }

                val rootGroups = allGroups.filter { group -> group.parentUid == null }
                val rootFlows = allFlows.filter { flow -> flow.groupUid == null }

                val (passed, failed, remained) = allFlows.aggregatePassedFailedAndRemainedFlows(
                    versionRuns
                )

                ProjectDashboardData(
                    versions = versions,
                    allRuns = allRuns,
                    allFlows = allFlows,
                    allGroups = allGroups,
                    versionRuns = versionRuns,
                    passedFlows = passed,
                    failedFlows = failed,
                    remainedFlows = remained,
                    rootGroups = rootGroups,
                    rootFlows = rootFlows
                )
            }
        }

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