package com.github.aivanovski.testswithme.android.data.api

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.HttpClientFactory.createHttpClient
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.entity.exception.ApiException
import com.github.aivanovski.testswithme.android.entity.exception.InvalidHttpStatusCodeException
import com.github.aivanovski.testswithme.android.entity.exception.NetworkException
import com.github.aivanovski.testswithme.android.entity.exception.NoAccountDataException
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.web.api.response.ErrorMessage
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

class HttpRequestExecutor(
    val authRepository: AuthRepository,
    val jsonSerializer: JsonSerializer,
    settings: Settings
) {

    val httpClient = AtomicReference(
        createHttpClient(
            isSslVerificationEnabled = !settings.isSslVerificationDisabled
        )
    )

    fun rebuild(isSslVerificationEnabled: Boolean) {
        httpClient.set(
            createHttpClient(
                isSslVerificationEnabled = isSslVerificationEnabled
            )
        )
    }

    fun HttpRequestBuilder.appendAuthHeader(authRepository: AuthRepository) {
        headers {
            append(HttpHeaders.Authorization, "Bearer ${authRepository.getAuthToken()}")
        }
    }

    suspend inline fun <reified T> get(
        url: String,
        isAuthenticateAutomatically: Boolean = true,
        isAppendAuthHeader: Boolean = true
    ): Either<ApiException, T> =
        sendRequest(
            type = RequestType.GET,
            url = url,
            isAuthenticateAutomatically = isAuthenticateAutomatically,
            block = {
                if (isAppendAuthHeader) {
                    appendAuthHeader(authRepository)
                }
            }
        )

    suspend inline fun <reified T> post(
        url: String,
        body: String,
        isAuthenticateAutomatically: Boolean = true,
        isAppendAuthHeader: Boolean = true
    ): Either<ApiException, T> =
        sendRequest(
            type = RequestType.POST,
            url = url,
            isAuthenticateAutomatically = isAuthenticateAutomatically,
            block = {
                if (isAppendAuthHeader) {
                    appendAuthHeader(authRepository)
                }

                contentType(ContentType.Application.Json)
                setBody(body)
            }
        )

    suspend inline fun <reified T> put(
        url: String,
        body: String,
        isAuthenticateAutomatically: Boolean = true,
        isAppendAuthHeader: Boolean = true
    ): Either<ApiException, T> =
        sendRequest(
            type = RequestType.PUT,
            url = url,
            isAuthenticateAutomatically = isAuthenticateAutomatically,
            block = {
                if (isAppendAuthHeader) {
                    appendAuthHeader(authRepository)
                }

                contentType(ContentType.Application.Json)
                setBody(body)
            }
        )

    suspend inline fun <reified T> sendRequest(
        type: RequestType,
        url: String,
        isAuthenticateAutomatically: Boolean,
        noinline block: HttpRequestBuilder.() -> Unit
    ): Either<ApiException, T> =
        either {
            var result: Either<ApiException, T>?
            var attemptCount = 1
            val client = httpClient.get()

            do {
                val response = client.send(
                    type = type,
                    url = url,
                    block = block
                ).bind()

                val status = response.status
                if ((attemptCount > 1 && status != HttpStatusCode.OK) ||
                    (!isAuthenticateAutomatically && status != HttpStatusCode.OK) ||
                    (status != HttpStatusCode.OK && status != HttpStatusCode.Unauthorized)
                ) {
                    val errorBody = response.bodyAsText()
                    val errorResponse = jsonSerializer.deserialize<ErrorMessage>(errorBody)
                    val message = errorResponse.getOrNull()

                    if (message != null) {
                        raise(InvalidHttpStatusCodeException(response.status, message.message))
                    } else {
                        raise(InvalidHttpStatusCodeException(response.status))
                    }
                }

                val body = response.bodyAsText()

                result = if (response.status == HttpStatusCode.OK) {
                    jsonSerializer.deserialize<T>(body)
                        .mapLeft { exception -> ApiException(cause = exception) }
                } else {
                    null
                }

                if (response.status == HttpStatusCode.Unauthorized) {
                    val account = authRepository.getAccount()
                        ?: raise(NoAccountDataException())

                    authRepository.login(account.name, account.password)
                        .mapLeft { exception -> ApiException(cause = exception) }
                        .bind()
                }

                attemptCount++
            } while (attemptCount <= 2 && response.status == HttpStatusCode.Unauthorized)

            result?.bind() ?: throw IllegalStateException()
        }

    suspend fun HttpClient.send(
        type: RequestType,
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): Either<ApiException, HttpResponse> =
        either {
            val response = try {
                when (type) {
                    RequestType.GET -> get(urlString = url, block = block)
                    RequestType.POST -> post(urlString = url, block = block)
                    RequestType.PUT -> put(urlString = url, block = block)
                }
            } catch (exception: IOException) {
                raise(NetworkException(cause = exception))
            }

            response
        }

    enum class RequestType {
        GET,
        POST,
        PUT
    }
}