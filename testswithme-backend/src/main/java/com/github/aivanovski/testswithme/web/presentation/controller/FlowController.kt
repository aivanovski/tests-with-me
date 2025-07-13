package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.extensions.sha256
import com.github.aivanovski.testswithme.extensions.trimLines
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.web.api.dto.FlowsItemDto
import com.github.aivanovski.testswithme.web.api.dto.Sha256HashDto
import com.github.aivanovski.testswithme.web.api.request.PostFlowRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateFlowRequest
import com.github.aivanovski.testswithme.web.api.response.DeleteFlowResponse
import com.github.aivanovski.testswithme.web.api.response.FlowResponse
import com.github.aivanovski.testswithme.web.api.response.FlowsResponse
import com.github.aivanovski.testswithme.web.api.response.PostFlowResponse
import com.github.aivanovski.testswithme.web.api.response.UpdateFlowResponse
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.domain.AccessResolver
import com.github.aivanovski.testswithme.web.domain.ReferenceResolver
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.BadRequestException
import com.github.aivanovski.testswithme.web.entity.exception.EntityAlreadyExistsException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidBase64String
import com.github.aivanovski.testswithme.web.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import com.github.aivanovski.testswithme.web.extensions.toDto
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID

class FlowController(
    private val flowRepository: FlowRepository,
    private val projectRepository: ProjectRepository,
    private val groupRepository: GroupRepository,
    private val referenceResolver: ReferenceResolver,
    private val accessResolver: AccessResolver
) {

    fun postFlow(
        user: User,
        request: PostFlowRequest
    ): Either<AppException, PostFlowResponse> =
        either {
            val (project, group) = referenceResolver.parseProjectAndGroup(
                reference = request.parent,
                user = user
            ).bind()

            val content = Base64Utils.decode(request.base64Content).getOrNull()
                ?: raise(InvalidBase64String())

            val parsedFlow = YamlParser().parse(content)
                .mapLeft { exception -> ParsingException(cause = exception) }
                .bind()

            validaNewFlowName(
                name = parsedFlow.name,
                user = user,
                project = project,
                group = group
            ).bind()

            validateInnerReferences(
                flow = parsedFlow,
                project = project
            ).bind()

            val flowUid = project.uid.append(Uid.generate())

            val flow = Flow(
                uid = flowUid,
                projectUid = project.uid,
                groupUid = group.uid,
                name = parsedFlow.name,
                contentHash = content.trimLines().sha256(),
                isDeleted = false
            )

            flowRepository.add(flow, content).bind()

            PostFlowResponse(
                id = flowUid.toString()
            )
        }

    fun updateFlow(
        user: User,
        uid: String,
        request: UpdateFlowRequest
    ): Either<AppException, UpdateFlowResponse> =
        either {
            val flowUid = Uid.parse(uid).getOrNull()
                ?: raise(InvalidParameterException(ID))
            val newParent = request.parent
            val newBase64Content = request.base64Content ?: StringUtils.EMPTY

            accessResolver.canModifyFlow(user, flowUid).bind()

            val oldFlow = flowRepository.getByUid(flowUid).bind()
            val oldContent = flowRepository.getFlowContent(flowUid).bind()

            val (project, group) = if (newParent != null) {
                referenceResolver.parseProjectAndGroup(
                    reference = newParent,
                    user = user
                ).bind()
            } else {
                val project = projectRepository.getByUid(oldFlow.projectUid).bind()
                val group = groupRepository.getByUid(oldFlow.groupUid).bind()

                (project to group)
            }

            val (content, name) = if (newBase64Content.isNotBlank()) {
                val newContent = Base64Utils.decode(newBase64Content).getOrNull()
                    ?: raise(InvalidBase64String())

                val parsedFlow = YamlParser().parse(newContent)
                    .mapLeft { exception -> ParsingException(cause = exception) }
                    .bind()

                validateFlowContentChanged(
                    oldContent = oldContent,
                    newContent = newContent
                ).bind()

                validateFlowNameUpdate(
                    name = parsedFlow.name,
                    flowUid = flowUid
                ).bind()

                validateInnerReferences(
                    flow = parsedFlow,
                    project = project
                ).bind()

                (newContent to parsedFlow.name)
            } else {
                (oldContent to oldFlow.name)
            }

            val newFlow = oldFlow.copy(
                projectUid = project.uid,
                groupUid = group.uid,
                name = name,
                contentHash = content.trimLines().sha256()
            )

            flowRepository.update(
                flow = newFlow,
                content = content
            )

            UpdateFlowResponse(newFlow.toDto(content = content))
        }

    fun getFlow(
        user: User,
        uid: String
    ): Either<AppException, FlowResponse> =
        either {
            val flowUid = Uid.parse(uid).getOrNull()
                ?: raise(InvalidParameterException(ID))

            accessResolver.canReadFlow(user, flowUid).bind()

            val flow = flowRepository.getByUid(flowUid).bind()
            val content = flowRepository.getFlowContent(flow.uid).bind()

            FlowResponse(flow.toDto(content = content))
        }

    fun getFlows(user: User): Either<AppException, FlowsResponse> =
        either {
            val flows = flowRepository.getFlowsByUserUid(user.uid).bind()

            val items = flows.map { flow ->
                FlowsItemDto(
                    id = flow.uid.toString(),
                    projectId = flow.projectUid.toString(),
                    groupId = flow.groupUid.toString(),
                    name = flow.name,
                    contentHash = flow.contentHash.toDto()
                )
            }

            FlowsResponse(items)
        }

    fun deleteFLow(
        user: User,
        flowUid: String
    ): Either<AppException, DeleteFlowResponse> =
        either {
            val uid = Uid.parse(flowUid).bind()
            val flow = flowRepository.getByUid(uid).bind()

            accessResolver.canModifyFlow(user, flowUid = uid).bind()

            flowRepository.update(
                flow.copy(
                    isDeleted = true
                )
            )

            DeleteFlowResponse(
                isSuccess = true
            )
        }

    private fun validaNewFlowName(
        name: String,
        user: User,
        project: Project,
        group: Group
    ): Either<AppException, Unit> =
        either {
            if (name.isBlank()) {
                raise(BadRequestException("Flow name is not specified"))
            }

            val flowsInGroup = flowRepository.getFlowsByProjectAndGroup(
                userUid = user.uid,
                projectUid = project.uid,
                groupUid = group.uid
            ).bind()

            val hasTheSameName = flowsInGroup.any { flow -> flow.name == name }
            if (hasTheSameName) {
                raise(EntityAlreadyExistsException(name))
            }
        }

    private fun validateFlowNameUpdate(
        name: String,
        flowUid: Uid
    ): Either<AppException, Unit> =
        either {
            val flow = flowRepository.getByUid(flowUid).bind()
            if (name == flow.name) {
                return@either
            }

            val project = projectRepository.getByUid(flow.projectUid).bind()
            val group = groupRepository.getByUid(flow.groupUid).bind()

            val otherFlowNames = flowRepository.getFlowsByProjectAndGroup(
                userUid = project.userUid,
                projectUid = project.uid,
                groupUid = group.uid
            )
                .bind()
                .filter { fl -> fl.uid != flowUid }
                .map { fl -> fl.name }
                .toSet()

            if (otherFlowNames.contains(name)) {
                raise(BadRequestException("Flow with same name already exists: $name"))
            }
        }

    private fun validateFlowContentChanged(
        oldContent: String,
        newContent: String
    ): Either<AppException, Unit> =
        either {
            if (oldContent.trimLines() == newContent.trimLines()) {
                raise(BadRequestException("No changes in flow content"))
            }
        }

    private fun validateInnerReferences(
        flow: YamlFlow,
        project: Project
    ): Either<AppException, Unit> =
        either {
            val innerFlowSteps = flow.steps.mapNotNull { step -> step as? FlowStep.RunFlow }

            for (step in innerFlowSteps) {
                referenceResolver.resolveFlowByPathOrName(
                    pathOrName = step.path,
                    projectUid = project.uid
                ).bind()
            }
        }

    private fun Hash.toDto(): Sha256HashDto = Sha256HashDto(this.value)
}