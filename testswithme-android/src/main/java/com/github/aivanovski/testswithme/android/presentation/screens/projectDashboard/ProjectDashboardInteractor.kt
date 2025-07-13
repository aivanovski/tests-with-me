package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.domain.VersionParser
import com.github.aivanovski.testswithme.android.domain.usecases.GetExternalApplicationDataUseCase
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.testswithme.android.extensions.filterByGroupUid
import com.github.aivanovski.testswithme.android.extensions.filterRemoteOnly
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardData
import com.github.aivanovski.testswithme.android.utils.aggregatePassedFailedAndRemainedFlows
import com.github.aivanovski.testswithme.android.utils.combineEitherFlows
import kotlinx.coroutines.flow.Flow

class ProjectDashboardInteractor(
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val flowRepository: FlowRepository,
    private val flowRunRepository: FlowRunRepository,
    private val versionParser: VersionParser,
    private val getAppDataUseCase: GetExternalApplicationDataUseCase
) {

    fun loadData(
        projectUid: String,
        versionName: String?
    ): Flow<Either<AppException, ProjectDashboardData>> =
        combineEitherFlows(
            projectRepository.getProjectsFlow(),
            groupRepository.getGroupsFlow(),
            flowRepository.getFlowsFlow(),
            flowRunRepository.getRunsFlow()
        ) { allProjects, allGroups, allFlows, allRuns ->
            either {
                val project = allProjects.firstOrNull { project ->
                    project.uid == projectUid
                } ?: raise(FailedToFindEntityByUidException(ProjectEntry::class, projectUid))

                val filteredGroups = allGroups.filter { group -> group.projectUid == projectUid }
                val filteredFlows = allFlows.filter { flow -> flow.projectUid == projectUid }
                    .filterRemoteOnly()

                val flowUids = filteredFlows
                    .map { flow -> flow.uid }
                    .toSet()

                val filteredRuns = allRuns
                    .filter { run -> run.flowUid in flowUids && !run.isExpired }

                val versions = getVersions(
                    project = project,
                    runs = filteredRuns
                )

                val selectedVersion = if (versionName != null) {
                    versions.firstOrNull { version -> version.name == versionName }
                } else {
                    versions.firstOrNull()
                }

                val versionRuns = if (selectedVersion != null) {
                    filteredRuns.filter { run -> run.appVersionName == selectedVersion.name }
                } else {
                    filteredRuns
                }

                val rootGroup = filteredGroups.firstOrNull { group -> group.parentUid == null }
                    ?: raise(
                        FailedToFindEntityException(
                            entityName = GroupEntry::class.java.simpleName,
                            fieldName = "parentUid",
                            fieldValue = "null"
                        )
                    )

                val visibleGroups =
                    filteredGroups.filter { group -> group.parentUid == rootGroup.uid }
                val visibleFlows = filteredFlows
                    .filterByGroupUid(rootGroup.uid)
                    .filterRemoteOnly()

                val (passed, failed, remained) =
                    filteredFlows.aggregatePassedFailedAndRemainedFlows(versionRuns)

                val installedAppData = getAppDataUseCase.getApplicationData(project.packageName)
                    .getOrNull()

                ProjectDashboardData(
                    project = project,
                    versions = versions,
                    allRuns = filteredRuns,
                    allFlows = filteredFlows,
                    allGroups = filteredGroups,
                    versionRuns = versionRuns,
                    passedFlows = passed,
                    failedFlows = failed,
                    remainedFlows = remained,
                    rootGroup = rootGroup,
                    visibleGroups = visibleGroups,
                    visibleFlows = visibleFlows,
                    installedAppData = installedAppData
                )
            }
        }

    private fun getVersions(
        project: ProjectEntry,
        runs: List<FlowRunEntry>
    ): List<AppVersion> {
        val versionsFromRuns = runs
            .map { run ->
                versionParser.parseVersions(
                    versionName = run.appVersionName,
                    versionCode = run.appVersionCode
                )
            }
            .distinct()
            .sortedByDescending { version -> version.code }

        val installedAppVersion = getAppDataUseCase.getApplicationData(project.packageName)
            .getOrNull()
            ?.appVersion

        return if (installedAppVersion != null && installedAppVersion !in versionsFromRuns) {
            versionsFromRuns.plus(installedAppVersion)
                .sortedByDescending { version -> version.code }
        } else {
            versionsFromRuns
        }
    }

    suspend fun requestProjectSync(
        projectUid: String
    ): Either<AppException, Unit> =
        projectRepository.requestSync(projectUid)
            .map { }
}