package com.github.aivanovski.testswithme.android.domain.flow

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException

class PathResolver(
    private val flowRepository: FlowRepository,
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository
) {

    suspend fun resolveFlowByName(
        projectUid: String,
        flowName: String
    ): Either<AppException, FlowEntry> =
        either {
            val flow = flowRepository.getFlows().bind()
                .filter { flow ->
                    flow.projectUid == projectUid &&
                        flow.name.contentEquals(flowName, ignoreCase = true)
                }
                .sortedByDescending { flow -> flow.id }
                .firstOrNull()
                ?: raise(AppException("Failed to resolve flow by name: $flowName"))

            flow
        }

    suspend fun resolveProjectAndGroupByPath(
        projectName: String?,
        groupName: String?
    ): Either<AppException, Pair<ProjectEntry?, Group?>> =
        either {
            val projects = projectRepository.getProjects().bind()
            val groups = groupRepository.getGroups().bind()

            val project = if (projectName != null) {
                projects.firstOrNull { project -> project.name == projectName }
            } else {
                null
            }

            val group = if (groupName != null) {
                groups.firstOrNull { group ->
                    if (project != null) {
                        group.projectUid == project.uid && group.name == groupName
                    } else {
                        group.name == groupName
                    }
                }
            } else {
                null
            }

            project to group
        }
}