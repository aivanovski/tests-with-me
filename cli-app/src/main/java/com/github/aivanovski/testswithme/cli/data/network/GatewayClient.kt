package com.github.aivanovski.testswithme.cli.data.network

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints.PORT
import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints.START_TEST
import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints.STATUS
import com.github.aivanovski.testswithme.android.driverServerApi.request.StartTestRequest
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.android.driverServerApi.response.StartTestResponse
import com.github.aivanovski.testswithme.cli.entity.exception.ApiException
import com.github.aivanovski.testswithme.cli.entity.exception.InvalidHttpStatusCodeException
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GatewayClient(
    private val executor: HttpRequestExecutor
) {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    suspend fun getStatus(): Either<ApiException, GetStatusResponse> =
        either {
            getAndParse<GetStatusResponse>("http://127.0.0.1:$PORT/$STATUS").bind()
        }

    suspend fun startTest(request: StartTestRequest): Either<ApiException, StartTestResponse> =
        either {
            postAndParse<StartTestResponse>(
                url = "http://127.0.0.1:$PORT/$START_TEST",
                body = json.encodeToString(request)
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
}