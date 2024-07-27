package com.github.aivanovski.testswithme.android.presentation.screens.groups

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupsInteractor(
    private val flowRepository: FlowRepository,
    private val groupRepository: GroupRepository,
    private val flowRunRepository: FlowRunRepository
) {

    suspend fun getData(
        projectUid: String,
        groupUid: String?
    ): Either<AppException, GroupsData> =
        withContext(Dispatchers.IO) {
            either {
                val allGroups = groupRepository.getGroups().bind()

                val filteredGroups = allGroups
                    .filter { group ->
                        group.projectUid == projectUid && group.parentUid == groupUid
                    }

                val selectedGroup = groupUid?.let {
                    allGroups.firstOrNull { group -> group.uid == groupUid }
                }

                val allFlows = flowRepository.getFlows().bind()
                val flows = allFlows
                    .filter { flow ->
                        flow.projectUid == projectUid && flow.groupUid == groupUid
                    }

                val flowUids = flows
                    .map { flow -> flow.uid }
                    .toSet()

                val allRuns = flowRunRepository.getRuns().bind()
                val runs = allRuns
                    .filter { run -> run.flowUid in flowUids }

                GroupsData(
                    allGroups = allGroups,
                    group = selectedGroup,
                    groups = filteredGroups,
                    allFlows = allFlows,
                    flows = flows,
                    allRuns = allRuns,
                    runs = runs
                )
            }
        }
}