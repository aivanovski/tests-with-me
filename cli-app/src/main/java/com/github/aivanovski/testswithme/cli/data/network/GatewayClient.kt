package com.github.aivanovski.testswithme.cli.data.network

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.GatewayEndpoints.JOB
import com.github.aivanovski.testswithme.android.driverServerApi.GatewayEndpoints.PORT
import com.github.aivanovski.testswithme.android.driverServerApi.GatewayEndpoints.START_TEST
import com.github.aivanovski.testswithme.android.driverServerApi.GatewayEndpoints.STATUS
import com.github.aivanovski.testswithme.android.driverServerApi.request.StartTestRequest
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetJobResponse
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.android.driverServerApi.response.StartTestResponse
import com.github.aivanovski.testswithme.cli.entity.exception.ApiException
import com.github.aivanovski.testswithme.cli.entity.exception.InvalidHttpStatusCodeException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GatewayClient {

    private val executor = HttpRequestExecutor(
        client = createHttpClient()
    )

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    suspend fun getStatus(): Either<ApiException, GetStatusResponse> =
        either {
            getAndParse<GetStatusResponse>(
                url = "$SERVER_URL/$STATUS"
            ).bind()
        }

    suspend fun startTest(request: StartTestRequest): Either<ApiException, StartTestResponse> =
        either {
            postAndParse<StartTestResponse>(
                url = "$SERVER_URL/$START_TEST",
                body = json.encodeToString(request)
            ).bind()
        }

    suspend fun getJob(jobId: String): Either<ApiException, GetJobResponse> =
        either {
            getAndParse<GetJobResponse>(
                url = "$SERVER_URL/$JOB/$jobId"
            ).bind()
        }

    private suspend inline fun <reified T> postAndParse(
        url: String,
        body: String
    ): Either<ApiException, T> =
        either {
            val response = executor.post(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.bind()

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            return parseJson<T>(response.body())
        }

    private suspend inline fun <reified T> getAndParse(url: String): Either<ApiException, T> =
        either {
            val response = executor.get(url, block = {}).bind()
            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            return parseJson<T>(response.body())
        }

    private inline fun <reified T> parseJson(body: String): Either<ApiException, T> =
        either {
            try {
                json.decodeFromString<T>(body)
            } catch (exception: SerializationException) {
                exception.printStackTrace()
                raise(ApiException(cause = exception))
            }
        }

    companion object {
        private const val SERVER_URL = "http://127.0.0.1:$PORT"

        private fun createHttpClient(): HttpClient {
            return HttpClient(OkHttp) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            println(message)
                        }
                    }
                    level = LogLevel.NONE
                }
            }
        }
    }
}