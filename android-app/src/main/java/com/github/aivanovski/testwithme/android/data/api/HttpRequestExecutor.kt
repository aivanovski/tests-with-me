package com.github.aivanovski.testwithme.android.data.api

import arrow.core.Either
import com.github.aivanovski.testwithme.android.entity.exception.ApiException
import com.github.aivanovski.testwithme.android.entity.exception.NetworkException
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class HttpRequestExecutor(
    private val client: HttpClient
) {

    suspend fun get(
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): Either<ApiException, HttpResponse> = withContext(Dispatchers.IO) {
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
    ): Either<ApiException, HttpResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.post(urlString = url, block = block)
            Either.Right(response)
        } catch (exception: IOException) {
            Either.Left(NetworkException(exception))
        }
    }
}