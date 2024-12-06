package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.extensions.sha256
import com.github.aivanovski.testswithme.extensions.trimLines
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.web.api.FlowItemDto
import com.github.aivanovski.testswithme.web.api.FlowsItemDto
import com.github.aivanovski.testswithme.web.api.Sha256HashDto
import com.github.aivanovski.testswithme.web.api.request.PostFlowRequest
import com.github.aivanovski.testswithme.web.api.response.FlowResponse
import com.github.aivanovski.testswithme.web.api.response.FlowsResponse
import com.github.aivanovski.testswithme.web.api.response.PostFlowResponse
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.domain.PathResolver
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.BadRequestException
import com.github.aivanovski.testswithme.web.entity.exception.EntityAlreadyExistsException
import com.github.aivanovski.testswithme.web.entity.exception.FlowNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidBase64String
import com.github.aivanovski.testswithme.web.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import com.github.aivanovski.testswithme.web.extensions.encodeToBase64
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID

class FlowController(
    private val flowRepository: FlowRepository,
    private val pathResolver: PathResolver
) {

    fun postFlow(
        user: User,
        request: PostFlowRequest
    ): Either<AppException, PostFlowResponse> =
        either {
            val (project, group) = pathResolver.resolveProjectAndGroup(
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

            val flowUid = project.uid.append(Uid.generate())

            val path = flowRepository.putFlowContent(
                flowUid = flowUid,
                projectUid = project.uid,
                content = content
            ).bind()

            val flow = Flow(
                uid = flowUid,
                projectUid = project.uid,
                groupUid = group.uid,
                name = parsedFlow.name,
                path = path,
                contentHash = content.trimLines().sha256()
            )

            flowRepository.add(flow).bind()

            PostFlowResponse(
                id = flowUid.toString()
            )
        }

    fun getFlow(
        user: User,
        flowUid: String
    ): Either<AppException, FlowResponse> =
        either {
            val uid = Uid.parse(flowUid).getOrNull()
                ?: raise(InvalidParameterException(ID))

            val allFlows = flowRepository.getFlowsByUserUid(user.uid).bind()

            val flows = allFlows.filter { flow -> flow.uid == uid }
            if (flows.isEmpty()) {
                raise(FlowNotFoundByUidException(flowUid))
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
                    base64Content = rawContent.encodeToBase64(),
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

    private fun Hash.toDto(): Sha256HashDto = Sha256HashDto(this.value)
}