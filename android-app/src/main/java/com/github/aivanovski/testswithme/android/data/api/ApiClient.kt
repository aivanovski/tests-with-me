package com.github.aivanovski.testswithme.android.data.api

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.User
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.ApiException
import com.github.aivanovski.testswithme.android.entity.exception.InvalidHttpStatusCodeException
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.web.api.request.LoginRequest
import com.github.aivanovski.testswithme.web.api.request.PostFlowRequest
import com.github.aivanovski.testswithme.web.api.request.PostFlowRunRequest
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.PostProjectRequest
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
import com.github.aivanovski.testswithme.web.api.response.UpdateGroupResponse
import com.github.aivanovski.testswithme.web.api.response.UsersResponse
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class ApiClient(
    private val executor: HttpRequestExecutor,
    private val settings: Settings
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    private val urlFactory = ApiUrlFactory()

    suspend fun getGroups(): Either<ApiException, List<Group>> =
        either {
            getAndParse<GroupsResponse>(urlFactory.groups())
                .bind()
                .groups
                .toGroups()
        }

    suspend fun getUsers(): Either<ApiException, List<User>> =
        either {
            getAndParse<UsersResponse>(urlFactory.users())
                .bind()
                .users
                .toUsers()
        }

    suspend fun postFlowRun(
        request: PostFlowRunRequest
    ): Either<ApiException, PostFlowRunResponse> =
        either {
            val body = json.encodeToString(request)

            // TODO: should be retried if 401
            val response = executor.post(urlFactory.flowRuns()) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${settings.authToken}")
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }.bind()

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            parseJson<PostFlowRunResponse>(response.bodyAsText()).bind()
        }

    suspend fun postFlow(request: PostFlowRequest): Either<ApiException, PostFlowResponse> =
        either {
            val body = json.encodeToString(request)

            // TODO: should be retried if 401
            val response = executor.post(urlFactory.flows()) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${settings.authToken}")
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }.bind()

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            parseJson<PostFlowResponse>(response.bodyAsText()).bind()
        }

    suspend fun postProject(
        request: PostProjectRequest
    ): Either<ApiException, PostProjectResponse> =
        either {
            val body = json.encodeToString(request)

            // TODO: should be retried if 401
            val response = executor.post(urlFactory.projects()) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${settings.authToken}")
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }.bind()

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            parseJson<PostProjectResponse>(response.bodyAsText()).bind()
        }

    suspend fun postGroup(request: PostGroupRequest): Either<ApiException, PostGroupResponse> =
        either {
            val body = json.encodeToString(request)

            // TODO: should be retried if 401
            val response = executor.post(urlFactory.groups()) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${settings.authToken}")
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }.bind()

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            parseJson<PostGroupResponse>(response.bodyAsText()).bind()
        }

    suspend fun putGroup(
        groupUid: String,
        request: UpdateGroupRequest
    ): Either<ApiException, UpdateGroupResponse> =
        either {
            val body = json.encodeToString(request)

            // TODO: refactor + retry if 401
            val response = executor.put(urlFactory.group(groupUid)) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${settings.authToken}")
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }.bind()

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            parseJson<UpdateGroupResponse>(response.bodyAsText()).bind()
        }

    suspend fun getFlowRuns(): Either<ApiException, List<FlowRun>> =
        either {
            getAndParse<FlowRunsResponse>(urlFactory.flowRuns())
                .bind()
                .stats
                .toFlowRuns()
        }

    suspend fun getProjects(): Either<ApiException, List<ProjectEntry>> =
        either {
            getAndParse<ProjectsResponse>(urlFactory.projects())
                .bind()
                .projects
                .toProjects()
        }

    suspend fun getFlows(): Either<ApiException, List<FlowEntry>> =
        either {
            getAndParse<FlowsResponse>(urlFactory.flows())
                .bind()
                .flows
                .toFlows()
        }

    suspend fun getFlow(flowUid: String): Either<ApiException, FlowResponse> =
        either {
            getAndParse<FlowResponse>(urlFactory.flow(flowUid)).bind()
        }

    suspend fun login(
        username: String,
        password: String
    ): Either<ApiException, LoginResponse> =
        either {
            val body = json.encodeToString(
                LoginRequest(
                    username = username,
                    password = password
                )
            )

            val response = executor.post(urlFactory.login()) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.bind()

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            return parseJson(response.bodyAsText())
        }

    private suspend inline fun <reified T> getAndParse(url: String): Either<ApiException, T> {
        val body = get(url)
        if (body.isLeft()) {
            return Either.Left(body.unwrapError())
        }

        return parseJson<T>(body.unwrap())
    }

    private suspend fun get(url: String): Either<ApiException, String> =
        either {
            // TODO: check if ktor could automatically retry request

            // Get token if necessary
            loadOrRequestAuthToken().bind()

            val requestBuilder: HttpRequestBuilder.() -> Unit = {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${settings.authToken}")
                }
                contentType(ContentType.Application.Json)
            }

            // Do request
            val response = executor.get(url, block = requestBuilder).bind()
            if (response.status == HttpStatusCode.OK) {
                return@either response.bodyAsText()
            }

            // Authenticate was unsuccessful, retry request
            if (response.status == HttpStatusCode.Unauthorized) {
                settings.authToken = null
                loadOrRequestAuthToken().bind()

                // Do request
                val retryResponse = executor.get(url, block = requestBuilder).bind()
                if (retryResponse.status != HttpStatusCode.OK) {
                    raise(InvalidHttpStatusCodeException(response.status))
                }

                retryResponse.bodyAsText()
            } else {
                raise(InvalidHttpStatusCodeException(response.status))
            }
        }

    private suspend fun loadOrRequestAuthToken(): Either<ApiException, String> =
        either {
            if (settings.authToken == null) {
                val token = login("admin", "abc123").bind().token // TODO: store credentials
                settings.authToken = token
                token
            } else {
                settings.authToken.orEmpty()
            }
        }

    private inline fun <reified T> parseJson(body: String): Either<ApiException, T> =
        either {
            try {
                json.decodeFromString<T>(body)
            } catch (exception: SerializationException) {
                Timber.d(exception)
                raise(ApiException(cause = exception))
            }
        }
}