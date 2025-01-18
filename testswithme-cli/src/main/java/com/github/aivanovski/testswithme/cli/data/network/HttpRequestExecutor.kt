package com.github.aivanovski.testswithme.cli.data.network

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.entity.exception.ApiException
import com.github.aivanovski.testswithme.cli.entity.exception.InvalidHttpStatusCodeException
import com.github.aivanovski.testswithme.cli.entity.exception.NetworkException
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpRequestExecutor(
    val jsonSerializer: JsonSerializer,
    val client: HttpClient
) {

    suspend inline fun <reified T> get(url: String): Either<ApiException, T> =
        either {
            val response = withContext(Dispatchers.IO) {
                try {
                    client.get(urlString = url)
                } catch (exception: IOException) {
                    raise(NetworkException(cause = exception))
                }
            }

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            jsonSerializer.deserialize<T>(response.body())
                .mapLeft { exception -> ApiException(cause = exception) }
                .bind()
        }

    suspend inline fun <reified T> post(
        url: String,
        body: String
    ): Either<ApiException, T> =
        either {
            val response = withContext(Dispatchers.IO) {
                try {
                    client.post(
                        urlString = url,
                        block = {
                            contentType(ContentType.Application.Json)
                            setBody(body)
                        }
                    )
                } catch (exception: IOException) {
                    raise(NetworkException(cause = exception))
                }
            }

            if (response.status != HttpStatusCode.OK) {
                raise(InvalidHttpStatusCodeException(response.status))
            }

            jsonSerializer.deserialize<T>(response.body())
                .mapLeft { exception -> ApiException(cause = exception) }
                .bind()
        }
}