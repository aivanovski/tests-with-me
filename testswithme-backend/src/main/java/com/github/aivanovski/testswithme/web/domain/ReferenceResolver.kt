package com.github.aivanovski.testswithme.web.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.flow.reference.ReferenceResolverUtils
import com.github.aivanovski.testswithme.flow.reference.ReferenceResolverUtils.resolveGroupsAndName
import com.github.aivanovski.testswithme.web.api.EntityReference
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.GroupPath
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByNameException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidAccessException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidUidString
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import com.github.aivanovski.testswithme.web.extensions.aggregateGroupsByParent
import java.util.LinkedList

class ReferenceResolver(
    private val flowRepository: FlowRepository,
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository
) {

    fun resolveFlowByPathOrName(
        pathOrName: String,
        projectUid: Uid
    ): Either<AppException, Flow> =
        either {
            val (groupNames, flowName) = resolveGroupsAndName(pathOrName)
                .mapLeft { exception -> AppException(cause = exception) }
                .bind()

            val group = if (groupNames.isNotEmpty()) {
                getGroup(groupNames.last(), projectUid).bind()
            } else {
                null
            }

            val allCandidates = flowRepository.getFlowsByProjectUid(projectUid).bind()

            val groupFlowUids = allCandidates
                .filter { flow -> flow.groupUid == group?.uid }
                .map { flow -> flow.uid }
                .toSet()

            val groupFlows = allCandidates.filter { flow -> flow.uid in groupFlowUids }
            val otherFlows = allCandidates.filter { flow -> flow.uid !in groupFlowUids }

            val candidates = (groupFlows + otherFlows)
                .filter { flow -> flow.name == flowName }

            candidates.firstOrNull()
                ?: raise(EntityNotFoundByNameException(Flow::class, flowName))
        }

    fun parseProjectAndGroup(
        reference: EntityReference,
        user: User
    ): Either<AppException, Pair<Project, Group>> =
        either {
            val path = reference.path
            val groupId = reference.groupId
            val projectId = reference.projectId

            when {
                path != null -> {
                    val (project, groups) = resolveProjectAndGroupsByPath(
                        path = path,
                        user = user
                    ).bind()

                    project to groups.last()
                }

                groupId != null -> {
                    val group = findGroupByUid(groupId, user).bind()
                    val project = projectRepository.getByUid(group.projectUid).bind()

                    project to group
                }

                projectId != null -> {
                    val project = getProject(projectId, user).bind()
                    val group = getGroup(
                        groupUid = project.rootGroupUid.toString(),
                        projectUid = project.uid
                    ).bind()

                    project to group
                }

                else -> {
                    raise(ParsingException("Failed to parse reference: $reference"))
                }
            }
        }

    fun resolveProjectAndGroup(
        path: String?,
        projectUid: String?,
        groupUid: String?,
        user: User
    ): Either<AppException, Pair<Project, Group>> =
        either {
            when {
                !path.isNullOrBlank() -> {
                    val (project, groups) = resolveProjectAndGroupsByPath(
                        path = path,
                        user = user
                    ).bind()

                    project to groups.last()
                }

                !projectUid.isNullOrBlank() -> {
                    val project = getProject(projectUid, user).bind()
                    val group = if (groupUid != null) {
                        getGroup(groupUid, project.uid).bind()
                    } else {
                        getGroup(project.rootGroupUid.toString(), project.uid).bind()
                    }

                    project to group
                }

                else -> {
                    raise(AppException("Unable to resolve"))
                }
            }
        }

    private fun findGroupByUid(
        groupUid: String,
        user: User
    ): Either<AppException, Group> =
        either {
            val uid = Uid.parse(groupUid).getOrNull()
                ?: raise(InvalidUidString(groupUid))

            groupRepository.getByUserUid(user.uid)
                .bind()
                .firstOrNull { group -> group.uid == uid }
                ?: raise(EntityNotFoundByUidException(Group::class, groupUid))
        }

    private fun getProject(
        projectUid: String,
        user: User
    ): Either<AppException, Project> =
        either {
            val uid = Uid.parse(projectUid).getOrNull()
                ?: raise(InvalidUidString(projectUid))

            val project = projectRepository.getByUid(uid).bind()

            if (project.userUid != user.uid) {
                raise(InvalidAccessException("Unable to access the project: ${project.name}"))
            }

            project
        }

    private fun getGroup(
        groupUid: String,
        projectUid: Uid
    ): Either<AppException, Group> =
        either {
            val uid = Uid.parse(groupUid).getOrNull()
                ?: raise(InvalidUidString(groupUid))

            val group = groupRepository.getByUid(uid).bind()

            if (group.projectUid != projectUid) {
                raise(InvalidAccessException("Failed to access the group: ${group.name}"))
            }

            group
        }

    private fun resolveProjectAndGroupsByPath(
        path: String,
        user: User
    ): Either<AppException, GroupPath> =
        either {
            val values = path
                .split("/")
                .mapNotNull { value ->
                    if (value.isNotBlank()) {
                        value.trim()
                    } else {
                        null
                    }
                }

            if (values.isEmpty()) {
                raise(AppException("Failed to parse path: $path"))
            }

            val projectName = values.first()
            val groupNames = if (values.size > 1) {
                values.subList(1, values.size)
            } else {
                emptyList()
            }

            val projects = projectRepository.getByUserUid(user.uid).bind()
            val project = resolveProjectByName(
                name = projectName,
                projects = projects
            ).bind()

            val groups = if (groupNames.isNotEmpty()) {
                val groups = groupRepository.getByUserUid(user.uid)
                    .bind()
                    .filter { group -> group.projectUid == project.uid }

                resolveGroupsByName(groupNames, groups).bind()
            } else {
                val rootGroup = groupRepository.getByUid(project.rootGroupUid).bind()

                listOf(rootGroup)
            }

            GroupPath(
                project = project,
                groups = groups
            )
        }

    private fun resolveProjectByName(
        name: String,
        projects: List<Project>
    ): Either<AppException, Project> =
        either {
            val projectsByName = projects.filter { project -> project.name == name }

            when {
                projectsByName.isEmpty() -> {
                    raise(EntityNotFoundByNameException(Project::class, name))
                }

                projectsByName.size > 1 -> {
                    raise(AppException("Failed to resolve project by name: $name"))
                }

                else -> {
                    projectsByName.first()
                }
            }
        }

    private fun resolveGroupByName(
        name: String,
        groups: List<Group>
    ): Either<AppException, Group> =
        either {
            val groupsByName = groups.filter { group -> group.name == name }

            when {
                groupsByName.isEmpty() -> {
                    raise(EntityNotFoundByNameException(Group::class, name))
                }

                groupsByName.size > 1 -> {
                    raise(AppException("Failed to resolve group by name: $name"))
                }

                else -> {
                    groupsByName.first()
                }
            }
        }

    private fun resolveGroupsByName(
        names: List<String>,
        groups: List<Group>
    ): Either<AppException, List<Group>> =
        either {
            val result = mutableListOf<Group>()
            val queue = LinkedList<String>()
                .apply {
                    addAll(names)
                }

            val parentToChildrenMap = groups.aggregateGroupsByParent()

            var currentGroupUid: String? = null
            while (queue.isNotEmpty()) {
                val name = queue.removeFirst()
                val children = parentToChildrenMap[currentGroupUid] ?: emptyList()
                val group = resolveGroupByName(name, children).bind()
                result.add(group)
                currentGroupUid = group.uid.toString()
            }

            result
        }
}