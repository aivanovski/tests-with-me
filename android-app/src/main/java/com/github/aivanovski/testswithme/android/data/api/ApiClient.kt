package com.github.aivanovski.testswithme.android.data.api

import arrow.core.Either
import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.User
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.ApiException
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.web.api.request.LoginRequest
import com.github.aivanovski.testswithme.web.api.request.PostFlowRequest
import com.github.aivanovski.testswithme.web.api.request.PostFlowRunRequest
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.PostProjectRequest
import com.github.aivanovski.testswithme.web.api.request.ResetFlowRunsRequest
import com.github.aivanovski.testswithme.web.api.request.SignUpRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import com.github.aivanovski.testswithme.web.api.response.FlowResponse
import com.github.aivanovski.testswithme.web.api.response.FlowRunsResponse
import com.github.aivanovski.testswithme.web.api.response.FlowsResponse
import com.github.aivanovski.testswithme.web.api.response.GroupsResponse
import com.github.aivanovski.testswithme.web.api.response.LoginResponse
import com.github.aivanovski.testswithme.web.api.response.PostFlowResponse
import com.github.aivanovski.testswithme.web.api.response.PostFlowRunResponse
import com.github.aivanovski.testswithme.web.api.response.PostGroupResponse
import com.github.aivanovski.testswithme.web.api.response.PostProjectResponse
import com.github.aivanovski.testswithme.web.api.response.ProjectsResponse
import com.github.aivanovski.testswithme.web.api.response.ResetFlowRunsResponse
import com.github.aivanovski.testswithme.web.api.response.SignUpResponse
import com.github.aivanovski.testswithme.web.api.response.UpdateGroupResponse
import com.github.aivanovski.testswithme.web.api.response.UsersResponse

class ApiClient(
    private val executor: HttpRequestExecutor,
    private val jsonSerializer: JsonSerializer
) {

    private val urlFactory = ApiUrlFactory()

    suspend fun getGroups(): Either<ApiException, List<GroupEntry>> =
        executor.get<GroupsResponse>(url = urlFactory.groups())
            .map { response -> response.groups.toGroups() }

    suspend fun getUsers(): Either<ApiException, List<User>> =
        executor.get<UsersResponse>(url = urlFactory.users())
            .map { response -> response.users.toUsers() }

    suspend fun postFlowRun(
        request: PostFlowRunRequest
    ): Either<ApiException, PostFlowRunResponse> =
        executor.post<PostFlowRunResponse>(
            url = urlFactory.flowRuns(),
            body = jsonSerializer.serialize(request)
        )

    suspend fun resetFlowRun(
        request: ResetFlowRunsRequest
    ): Either<ApiException, ResetFlowRunsResponse> =
        executor.post<ResetFlowRunsResponse>(
            url = urlFactory.resetFlowRun(),
            body = jsonSerializer.serialize(request)
        )

    suspend fun postFlow(request: PostFlowRequest): Either<ApiException, PostFlowResponse> =
        executor.post<PostFlowResponse>(
            url = urlFactory.flows(),
            body = jsonSerializer.serialize(request)
        )

    suspend fun postProject(
        request: PostProjectRequest
    ): Either<ApiException, PostProjectResponse> =
        executor.post<PostProjectResponse>(
            url = urlFactory.projects(),
            body = jsonSerializer.serialize(request)
        )

    suspend fun postGroup(request: PostGroupRequest): Either<ApiException, PostGroupResponse> =
        executor.post<PostGroupResponse>(
            url = urlFactory.groups(),
            body = jsonSerializer.serialize(request)
        )

    suspend fun putGroup(
        groupUid: String,
        request: UpdateGroupRequest
    ): Either<ApiException, UpdateGroupResponse> =
        executor.put<UpdateGroupResponse>(
            url = urlFactory.group(groupUid),
            body = jsonSerializer.serialize(request)
        )

    suspend fun getFlowRuns(): Either<ApiException, List<FlowRun>> =
        executor.get<FlowRunsResponse>(urlFactory.flowRuns())
            .map { response -> response.stats.toFlowRuns() }

    suspend fun getProjects(): Either<ApiException, List<ProjectEntry>> =
        executor.get<ProjectsResponse>(urlFactory.projects())
            .map { response -> response.projects.toProjects() }

    suspend fun getFlows(): Either<ApiException, List<FlowEntry>> =
        executor.get<FlowsResponse>(urlFactory.flows())
            .map { response -> response.flows.toFlows() }

    suspend fun getFlow(flowUid: String): Either<ApiException, FlowResponse> =
        executor.get<FlowResponse>(urlFactory.flow(flowUid))

    suspend fun login(request: LoginRequest): Either<ApiException, LoginResponse> =
        executor.post<LoginResponse>(
            url = urlFactory.login(),
            body = jsonSerializer.serialize(request),
            isAuthenticateAutomatically = false,
            isAppendAuthHeader = false
        )

    suspend fun signUp(request: SignUpRequest): Either<ApiException, SignUpResponse> =
        executor.post<SignUpResponse>(
            url = urlFactory.signUp(),
            body = jsonSerializer.serialize(request),
            isAuthenticateAutomatically = false,
            isAppendAuthHeader = false
        )
}