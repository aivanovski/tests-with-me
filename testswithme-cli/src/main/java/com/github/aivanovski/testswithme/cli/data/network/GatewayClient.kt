package com.github.aivanovski.testswithme.cli.data.network

import arrow.core.Either
import com.github.aivanovski.testswithme.android.gatewayServerApi.GatewayEndpoints.JOB
import com.github.aivanovski.testswithme.android.gatewayServerApi.GatewayEndpoints.PORT
import com.github.aivanovski.testswithme.android.gatewayServerApi.GatewayEndpoints.START_TEST
import com.github.aivanovski.testswithme.android.gatewayServerApi.GatewayEndpoints.STATUS
import com.github.aivanovski.testswithme.android.gatewayServerApi.request.StartTestRequest
import com.github.aivanovski.testswithme.android.gatewayServerApi.response.GetJobResponse
import com.github.aivanovski.testswithme.android.gatewayServerApi.response.GetStatusResponse
import com.github.aivanovski.testswithme.android.gatewayServerApi.response.StartTestResponse
import com.github.aivanovski.testswithme.cli.entity.exception.ApiException
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.slf4j.LoggerFactory

class GatewayClient(
    private val jsonSerializer: JsonSerializer
) {

    private val executor = HttpRequestExecutor(
        jsonSerializer = jsonSerializer,
        client = createHttpClient()
    )

    suspend fun getStatus(): Either<ApiException, GetStatusResponse> =
        executor.get(
            url = "$SERVER_URL/$STATUS"
        )

    suspend fun startTest(request: StartTestRequest): Either<ApiException, StartTestResponse> =
        executor.post(
            url = "$SERVER_URL/$START_TEST",
            body = jsonSerializer.serialize(request)
        )

    suspend fun getJob(jobId: String): Either<ApiException, GetJobResponse> =
        executor.get(
            url = "$SERVER_URL/$JOB/$jobId"
        )

    companion object {
        private const val SERVER_URL = "http://127.0.0.1:$PORT"

        private val fileLogger = LoggerFactory.getLogger(GatewayClient::class.java)

        private fun createHttpClient(): HttpClient {
            return HttpClient(OkHttp) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            fileLogger.debug(message)
                        }
                    }
                    level = LogLevel.NONE
                }
            }
        }
    }
}