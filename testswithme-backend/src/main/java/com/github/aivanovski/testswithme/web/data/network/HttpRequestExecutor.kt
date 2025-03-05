package com.github.aivanovski.testswithme.web.data.network

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.web.entity.exception.NetworkRequestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import java.io.IOException

class HttpRequestExecutor(
    val jsonSerializer: JsonSerializer,
    val client: HttpClient
) {

    suspend inline fun <reified T> get(
        url: String
    ): Either<NetworkRequestException, T> =
        either {
            val response = try {
                client.get(urlString = url)
            } catch (exception: IOException) {
                raise(NetworkRequestException(cause = exception))
            }

            if (response.status != HttpStatusCode.OK) {
                raise(NetworkRequestException("Invalid HTTP status code: ${response.status}"))
            }

            jsonSerializer.deserialize<T>(response.body())
                .mapLeft { exception -> NetworkRequestException(cause = exception) }
                .bind()
        }
}