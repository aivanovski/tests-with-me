package com.github.aivanovski.testswithme.android.data.api

import arrow.core.Either
import com.github.aivanovski.testswithme.android.entity.exception.ApiException
import com.github.aivanovski.testswithme.android.entity.exception.NetworkException
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpRequestExecutor(
    private val client: HttpClient
) {

    suspend fun get(
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): Either<ApiException, HttpResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get(urlString = url, block = block)
                Either.Right(response)
            } catch (exception: IOException) {
                Either.Left(NetworkException(exception))
            }
        }

    suspend fun post(
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): Either<ApiException, HttpResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(urlString = url, block = block)
                Either.Right(response)
            } catch (exception: IOException) {
                Either.Left(NetworkException(exception))
            }
        }

    suspend fun put(
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): Either<ApiException, HttpResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.put(urlString = url, block = block)
                Either.Right(response)
            } catch (exception: IOException) {
                Either.Left(NetworkException(exception))
            }
        }
}