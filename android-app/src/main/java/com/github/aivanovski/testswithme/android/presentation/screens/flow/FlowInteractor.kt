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
import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.OnFinishAction
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.ExternalAppData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.utils.aggregateDescendantFlows
import com.github.aivanovski.testswithme.android.utils.aggregateRunCountByFlowUid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlowInteractor(
    private val testInteractor: FlowRunnerInteractor,
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

    suspend fun loadData(mode: FlowScreenMode): Either<AppException, FlowData> =
        withContext(Dispatchers.IO) {
            either {
                val projectUid = resolveProjectUid(mode).bind()
                val project = projectRepository.getProjectByUid(projectUid).bind()

                val allGroups = groupRepository.getGroupsByProjectUid(projectUid).bind()
                val allFlows = flowRepository.getFlowsByProjectUid(projectUid).bind()

                val flowUids = allFlows.map { flow -> flow.uid }
                val allRuns = flowRunRepository.getRuns()
                    .bind()
                    .filter { run -> run.flowUid in flowUids }

                val group = if (mode is FlowScreenMode.Group) {
                    allGroups
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
                            allGroups = allGroups,
                            allFlows = allFlows,
                            groupUid = mode.groupUid
                        ).bind()
                    }

                    is FlowScreenMode.RemainedFlows -> {
                        getRemainedFlows(
                            allFlows = allFlows,
                            allRuns = allRuns,
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
                            allRuns.filter { run ->
                                run.flowUid in visibleFlowUids &&
                                    run.appVersionName == mode.requiredVersion.name
                            }
                        } else {
                            allRuns.filter { run ->
                                run.flowUid in visibleFlowUids
                            }
                        }
                    }

                    is FlowScreenMode.Group -> {
                        allRuns.filter { run -> run.flowUid in visibleFlowUids }
                    }

                    else -> emptyList()
                }

                val allUsers = userRepository.getUsers().bind()

                FlowData(
                    project = project,
                    allGroups = allGroups,
                    allRuns = allRuns,
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
        allRuns: List<FlowRun>,
        version: AppVersion?
    ): Either<AppException, List<FlowEntry>> =
        either {
            val filteredRuns = if (version != null) {
                allRuns.filter { run -> run.appVersionName == version.name }
            } else {
                allRuns
            }

            val flowUidToRunCount = filteredRuns.aggregateRunCountByFlowUid()

            allFlows.filter { flow ->
                val runs = flowUidToRunCount[flow.uid] ?: 0

                runs == 0
            }
        }

    private suspend fun resolveProjectUid(data: FlowScreenMode): Either<AppException, String> =
        either {
            when (data) {
                is FlowScreenMode.Flow -> {
                    flowRepository.getFlowByUid(data.flowUid)
                        .bind()
                        .entry
                        .projectUid
                }

                is FlowScreenMode.Group -> {
                    groupRepository.getGroupByUid(data.groupUid)
                        .bind()
                        .projectUid
                }

                is FlowScreenMode.RemainedFlows -> {
                    data.projectUid
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
                val currentJobs = testInteractor.getJobs().bind()

                currentJobs
                    .filter { job -> job.status == JobStatus.PENDING }
                    .onEach { job -> testInteractor.removeJob(job.uid) }

                for (index in flowUids.indices.reversed()) {
                    val flowUid = flowUids[index]
                    val jobUid = jobUids[index]

                    val onFinishAction = if (flowUids.size > 1 && index < flowUids.lastIndex) {
                        OnFinishAction.RUN_NEXT
                    } else {
                        OnFinishAction.SHOW_DETAILS
                    }

                    testInteractor.addFlowToJobQueue(
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
                val getJobResult = testInteractor.getJobByUid(jobUid)
                if (getJobResult.isLeft()) {
                    return@either
                }

                val job = getJobResult.bind()
                testInteractor.updateJob(
                    job.copy(
                        status = JobStatus.CANCELLED
                    )
                ).bind()
            }
        }
}