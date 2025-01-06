package com.github.aivanovski.testswithme.android.presentation.screens.groups

import arrow.core.Either
import arrow.core.right
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsData
import com.github.aivanovski.testswithme.android.utils.combineEitherFlows
import kotlinx.coroutines.flow.Flow

class GroupsInteractor(
    private val flowRepository: FlowRepository,
    private val groupRepository: GroupRepository,
    private val flowRunRepository: FlowRunRepository
) {

    fun loadDataFlow(
        projectUid: String,
        groupUid: String?
    ): Flow<Either<AppException, GroupsData>> =
        combineEitherFlows(
            groupRepository.getGroupsFlow(),
            flowRepository.getFlowsFlow(),
            flowRunRepository.getRunsFlow()
        ) { allGroups, allFlows, allRuns ->
            val filteredGroups = allGroups
                .filter { group ->
                    group.projectUid == projectUid && group.parentUid == groupUid
                }

            val selectedGroup = groupUid?.let {
                allGroups.firstOrNull { group -> group.uid == groupUid }
            }

            val flows = allFlows
                .filter { flow ->
                    flow.projectUid == projectUid && flow.groupUid == groupUid
                }

            val flowUids = flows
                .map { flow -> flow.uid }
                .toSet()

            val runs = allRuns.filter { run -> run.flowUid in flowUids }

            GroupsData(
                allGroups = allGroups,
                group = selectedGroup,
                groups = filteredGroups,
                allFlows = allFlows,
                flows = flows,
                allRuns = allRuns,
                runs = runs
            ).right()
        }

    suspend fun removeGroup(groupUid: String): Either<AppException, Unit> =
        groupRepository.removeByUid(groupUid)

    suspend fun removeFlow(flowUid: String): Either<AppException, Unit> =
        flowRepository.removeByUid(flowUid)
}