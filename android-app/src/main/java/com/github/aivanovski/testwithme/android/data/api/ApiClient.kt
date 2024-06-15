package com.github.aivanovski.testwithme.android.data.api

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.Settings
import com.github.aivanovski.testwithme.android.data.api.Api.buildGetFlowUrl
import com.github.aivanovski.testwithme.android.data.api.Api.buildLoginUrl
import com.github.aivanovski.testwithme.android.entity.exception.ApiException
import com.github.aivanovski.testwithme.android.entity.exception.InvalidHttpStatusCodeException
import com.github.aivanovski.testwithme.web.api.request.LoginRequest
import com.github.aivanovski.testwithme.web.api.response.FlowResponse
import com.github.aivanovski.testwithme.web.api.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class ApiClient(
    private val httpClient: HttpClient,
    private val settings: Settings
) {

    suspend fun getFlow(
        flowUid: String
    ): Either<ApiException, FlowResponse> = either {
        val body = get(buildGetFlowUrl(flowUid)).bind()
        parseJson<FlowResponse>(body).bind()
    }

    suspend fun get(url: String): Either<ApiException, String> = either {
        // Get token if necessary
        var token = if (settings.authToken == null) {
            val response = login().bind()

            response.token
        } else {
            settings.authToken.orEmpty()
        }

        settings.authToken = token

        // Do request
        val response = httpClient.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            return@either response.bodyAsText()
        }

        // Authenticate was unsuccessful, retry request
        if (response.status == HttpStatusCode.Unauthorized) {
            val loginResponse = login().bind()

            token = loginResponse.token
            settings.authToken = token

            // Do request
            val retryResponse = httpClient.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
            }

            if (retryResponse.status == HttpStatusCode.OK) {
                retryResponse.bodyAsText()
            } else {
                raise(InvalidHttpStatusCodeException(response.status))
            }
        } else {
            raise(InvalidHttpStatusCodeException(response.status))
        }
    }

    suspend fun login(): Either<ApiException, LoginResponse> = either {
        val body = Json.encodeToString(
            LoginRequest(
                username = "admin",
                password = "abc123"
            )
        )

        val response = httpClient.post(buildLoginUrl()) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

        if (response.status != HttpStatusCode.OK) {
            raise(InvalidHttpStatusCodeException(response.status))
        }

        return parseJson(response.bodyAsText())
    }

    private inline fun <reified T> parseJson(
        body: String
    ): Either<ApiException, T> = either {
        try {
            Json.decodeFromString<T>(body)
        } catch (exception: SerializationException) {
            Timber.d(exception)
            raise(ApiException(cause = exception))
        }
    }
}