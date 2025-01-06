package com.github.aivanovski.testswithme.android.presentation.screens.flow

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.data.repository.UserRepository
import com.github.aivanovski.testswithme.android.domain.buildGroupTree
import com.github.aivanovski.testswithme.android.domain.findNodeByUid
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerInteractor
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.domain.usecases.GetExternalApplicationDataUseCase
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.DriverServiceState
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.OnFinishAction
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.android.extensions.filterByProjectUid
import com.github.aivanovski.testswithme.android.extensions.filterGroupsByProjectUid
import com.github.aivanovski.testswithme.android.extensions.filterRemoteOnly
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.ExternalAppData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.utils.aggregateDescendantFlows
import com.github.aivanovski.testswithme.android.utils.aggregateRunCountByFlowUid
import com.github.aivanovski.testswithme.android.utils.combineEitherFlows
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FlowInteractor(
    private val flowRunnerInteractor: FlowRunnerInteractor,
    private val flowRepository: FlowRepository,
    private val projectRepository: ProjectRepository,
    private val flowRunRepository: FlowRunRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val getAppDataUseCase: GetExternalApplicationDataUseCase
) {

    fun isDriverServiceRunning(): Boolean {
        return FlowRunnerManager.getDriverState() == DriverServiceState.RUNNING
    }

    fun loadData(mode: FlowScreenMode): Flow<Either<AppException, FlowData>> =
        combineEitherFlows(
            projectRepository.getProjectsFlow(),
            groupRepository.getGroupsFlow(),
            flowRepository.getFlowsFlow(),
            flowRunRepository.getRunsFlow(),
            userRepository.getUsersFlow()
        ) { allProjects, allGroups, allFlows, allRuns, allUsers ->
            either {
                val projectUid = resolveProjectUid(mode, allFlows, allGroups).bind()
                val project = allProjects
                    .firstOrNull { project -> project.uid == projectUid }
                    ?: raise(FailedToFindEntityByUidException(ProjectEntry::class, projectUid))

                val groups = allGroups.filterGroupsByProjectUid(projectUid = projectUid)

                val flows = allFlows
                    .filterByProjectUid(projectUid = projectUid)
                    .filterRemoteOnly()

                val flowUids = flows
                    .map { flow -> flow.uid }
                    .toSet()

                val runs = allRuns.filter { run -> run.flowUid in flowUids }

                val group = if (mode is FlowScreenMode.Group) {
                    groups
                        .firstOrNull { group -> group.uid == mode.groupUid }
                        ?: raise(FailedToFindEntityByUidException(GroupEntry::class, mode.groupUid))
                } else {
                    null
                }

                val visibleFlows = when (mode) {
                    is FlowScreenMode.Flow -> {
                        val flow = flowRepository.getCachedFlowByUid(mode.flowUid).bind()
                        listOf(flow.entry)
                    }

                    is FlowScreenMode.Group -> {
                        getGroupFlows(
                            allGroups = groups,
                            allFlows = flows,
                            groupUid = mode.groupUid
                        ).bind()
                    }

                    is FlowScreenMode.RemainedFlows -> {
                        getRemainedFlows(
                            allFlows = flows,
                            allRuns = runs,
                            version = mode.version
                        ).bind()
                    }
                }

                val visibleFlowUids = visibleFlows
                    .map { flow -> flow.uid }
                    .toSet()

                val visibleRuns = when (mode) {
                    is FlowScreenMode.Flow -> {
                        if (mode.requiredVersion != null) {
                            runs.filter { run ->
                                run.flowUid in visibleFlowUids &&
                                    run.appVersionName == mode.requiredVersion.name
                            }
                        } else {
                            runs.filter { run ->
                                run.flowUid in visibleFlowUids
                            }
                        }
                    }

                    is FlowScreenMode.Group -> {
                        runs.filter { run -> run.flowUid in visibleFlowUids }
                    }

                    else -> emptyList()
                }

                FlowData(
                    project = project,
                    allGroups = groups,
                    allRuns = runs,
                    group = group,
                    visibleFlows = visibleFlows,
                    visibleRuns = visibleRuns,
                    allUsers = allUsers
                )
            }
        }

    private fun getGroupFlows(
        allGroups: List<GroupEntry>,
        allFlows: List<FlowEntry>,
        groupUid: String
    ): Either<AppException, List<FlowEntry>> =
        either {
            val tree = allGroups.buildGroupTree()
            val groupNode = tree.findNodeByUid(groupUid)
                ?: raise(FailedToFindEntityByUidException(GroupEntry::class, groupUid))

            groupNode.aggregateDescendantFlows(allFlows)
        }

    private fun getRemainedFlows(
        allFlows: List<FlowEntry>,
        allRuns: List<FlowRunEntry>,
        version: AppVersion?
    ): Either<AppException, List<FlowEntry>> =
        either {
            val filteredRuns = if (version != null) {
                allRuns.filter { run ->
                    !run.isExpired && run.appVersionName == version.name
                }
            } else {
                allRuns
            }

            val flowUidToRunCount = filteredRuns.aggregateRunCountByFlowUid()

            allFlows.filter { flow ->
                val runs = flowUidToRunCount[flow.uid] ?: 0

                runs == 0
            }
        }

    private fun resolveProjectUid(
        mode: FlowScreenMode,
        allFlows: List<FlowEntry>,
        allGroups: List<GroupEntry>
    ): Either<AppException, String> =
        either {
            when (mode) {
                is FlowScreenMode.Flow -> {
                    allFlows.firstOrNull { flow -> flow.uid == mode.flowUid }
                        ?.projectUid
                        ?: raise(FailedToFindEntityByUidException(FlowEntry::class, mode.flowUid))
                }

                is FlowScreenMode.Group -> {
                    allGroups.firstOrNull { group -> group.uid == mode.groupUid }
                        ?.projectUid
                        ?: raise(FailedToFindEntityByUidException(GroupEntry::class, mode.groupUid))
                }

                is FlowScreenMode.RemainedFlows -> {
                    mode.projectUid
                }
            }
        }

    fun getApplicationData(packageName: String): Either<AppException, ExternalAppData> =
        either {
            getAppDataUseCase.getApplicationData(packageName).bind()
        }

    suspend fun startFlows(
        flowUids: List<String>,
        jobUids: List<String>
    ): Either<AppException, List<String>> =
        withContext(Dispatchers.IO) {
            either {
                flowRunnerInteractor.removeAllJobs()

                for (index in flowUids.indices.reversed()) {
                    val flowUid = flowUids[index]
                    val jobUid = jobUids[index]

                    val onFinishAction = if (flowUids.size > 1 && index < flowUids.lastIndex) {
                        OnFinishAction.RUN_NEXT
                    } else {
                        OnFinishAction.SHOW_DETAILS
                    }

                    flowRunnerInteractor.addFlowToJobQueue(
                        flowUid = flowUid,
                        jobUid = jobUid,
                        onFinishAction = onFinishAction
                    ).bind()
                }

                jobUids
            }
        }

    suspend fun startFlow(
        flowUid: String,
        jobUid: String
    ): Either<AppException, String> =
        either {
            startFlows(
                flowUids = listOf(flowUid),
                jobUids = listOf(jobUid)
            ).bind()
                .first()
        }

    suspend fun cancelJob(jobUid: String): Either<AppException, Unit> =
        withContext(Dispatchers.IO) {
            either {
                val getJobResult = flowRunnerInteractor.getJobByUid(jobUid)
                if (getJobResult.isLeft()) {
                    return@either
                }

                val job = getJobResult.bind()
                flowRunnerInteractor.updateJob(
                    job.copy(
                        status = JobStatus.CANCELLED
                    )
                ).bind()
            }
        }
}