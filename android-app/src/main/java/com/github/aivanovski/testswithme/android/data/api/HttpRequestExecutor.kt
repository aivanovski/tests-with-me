package com.github.aivanovski.testswithme.android.data.api

import arrow.core.Either
import com.github.aivanovski.testswithme.android.data.api.HttpClientFactory.createHttpClient
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.entity.exception.ApiException
import com.github.aivanovski.testswithme.android.entity.exception.NetworkException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpRequestExecutor(
    settings: Settings
) {

    private val httpClient = AtomicReference(
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

    suspend fun get(
        url: String,
        block: HttpRequestBuilder.() -> Unit
    ): Either<ApiException, HttpResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get().get(urlString = url, block = block)
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
                val response = httpClient.get().post(urlString = url, block = block)
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
                val response = httpClient.get().put(urlString = url, block = block)
                Either.Right(response)
            } catch (exception: IOException) {
                Either.Left(NetworkException(exception))
            }
        }
}