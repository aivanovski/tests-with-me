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
import com.github.aivanovski.testswithme.web.api.dto.FlowItemDto
import com.github.aivanovski.testswithme.web.api.dto.FlowsItemDto
import com.github.aivanovski.testswithme.web.api.dto.Sha256HashDto
import com.github.aivanovski.testswithme.web.api.request.PostFlowRequest
import com.github.aivanovski.testswithme.web.api.response.DeleteFlowResponse
import com.github.aivanovski.testswithme.web.api.response.FlowResponse
import com.github.aivanovski.testswithme.web.api.response.FlowsResponse
import com.github.aivanovski.testswithme.web.api.response.PostFlowResponse
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
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
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidBase64String
import com.github.aivanovski.testswithme.web.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID

class FlowController(
    private val flowRepository: FlowRepository,
    private val referenceResolver: ReferenceResolver,
    private val accessResolver: AccessResolver
) {

    fun postFlow(
        user: User,
        request: PostFlowRequest
    ): Either<AppException, PostFlowResponse> =
        either {
            val (project, group) = referenceResolver.resolveProjectAndGroup(
                path = request.path,
                projectUid = request.projectId,
                groupUid = request.groupId,
                user = user
            ).bind()

            val content = Base64Utils.decode(request.base64Content).getOrNull()
                ?: raise(InvalidBase64String())

            val parsedFlow = YamlParser().parse(content)
                .mapLeft { exception -> ParsingException(cause = exception) }
                .bind()

            validateFlowName(
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

    fun getFlow(
        user: User,
        rawUid: String
    ): Either<AppException, FlowResponse> =
        either {
            val uid = Uid.parse(rawUid).getOrNull()
                ?: raise(InvalidParameterException(ID))

            val allFlows = flowRepository.getFlowsByUserUid(user.uid).bind()

            val flows = allFlows.filter { flow -> flow.uid == uid }
            if (flows.isEmpty()) {
                raise(EntityNotFoundByUidException(Flow::class, uid))
            }

            val flow = flows.first()
            val rawContent = flowRepository.getFlowContent(flow.uid).bind()
            val hash = rawContent.trimLines().sha256()

            FlowResponse(
                FlowItemDto(
                    id = flow.uid.toString(),
                    projectId = flow.projectUid.toString(),
                    groupId = flow.groupUid.toString(),
                    name = flow.name,
                    base64Content = Base64Utils.encode(rawContent),
                    contentHash = hash.toDto()
                )
            )
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

    private fun validateFlowName(
        name: String,
        user: User,
        project: Project,
        group: Group?
    ): Either<AppException, Unit> =
        either {
            if (name.isBlank()) {
                raise(BadRequestException("Flow name is not specified"))
            }

            val flowsInGroup = flowRepository.getFlowsByProjectAndGroup(
                userUid = user.uid,
                projectUid = project.uid,
                groupUid = group?.uid
            ).bind()

            val hasTheSameName = flowsInGroup.any { flow -> flow.name == name }
            if (hasTheSameName) {
                raise(EntityAlreadyExistsException(name))
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