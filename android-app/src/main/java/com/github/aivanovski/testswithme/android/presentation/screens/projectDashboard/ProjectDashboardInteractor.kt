package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.domain.VersionParser
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.testswithme.android.extensions.filterByGroupUid
import com.github.aivanovski.testswithme.android.extensions.filterRemoteOnly
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardData
import com.github.aivanovski.testswithme.android.utils.aggregatePassedFailedAndRemainedFlows

class ProjectDashboardInteractor(
    private val groupRepository: GroupRepository,
    private val flowRepository: FlowRepository,
    private val flowRunRepository: FlowRunRepository,
    private val versionParser: VersionParser
) {

    suspend fun loadData(
        projectUid: String,
        versionName: String?
    ): Either<AppException, ProjectDashboardData> =
        either {
            val allGroups = groupRepository.getGroupsByProjectUid(projectUid).bind()
            val allFlows = flowRepository.getFlowsByProjectUid(projectUid).bind()
                .filterRemoteOnly()

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

            val rootGroup = allGroups.firstOrNull { group -> group.parentUid == null }
                ?: raise(
                    FailedToFindEntityException(
                        entityName = GroupEntry::class.java.simpleName,
                        fieldName = "parentUid",
                        fieldValue = "null"
                    )
                )

            val visibleGroups = allGroups.filter { group -> group.parentUid == rootGroup.uid }
            val visibleFlows = allFlows
                .filterByGroupUid(rootGroup.uid)
                .filterRemoteOnly()

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
                rootGroup = rootGroup,
                visibleGroups = visibleGroups,
                visibleFlows = visibleFlows
            )
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