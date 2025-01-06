package com.github.aivanovski.testswithme.android.data.api

import android.annotation.SuppressLint
import com.github.aivanovski.testswithme.android.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import timber.log.Timber

object HttpClientFactory {

    fun createHttpClient(isSslVerificationEnabled: Boolean): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.INFO
            }

            if (BuildConfig.DEBUG && !isSslVerificationEnabled) {
                Timber.w("--------------------------------------------")
                Timber.w("--                                        --")
                Timber.w("--                                        --")
                Timber.w("-- SSL Certificate validation is disabled --")
                Timber.w("--                                        --")
                Timber.w("--                                        --")
                Timber.w("--------------------------------------------")
                disableSslVerification()
            }
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.disableSslVerification() {
        @SuppressLint("CustomX509TrustManager")
        val trustAllCerts = object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }

        val sslSocketFactory = SSLContext.getInstance("SSL")
            .apply {
                init(null, arrayOf(trustAllCerts), SecureRandom())
            }
            .socketFactory

        engine {
            config {
                sslSocketFactory(sslSocketFactory, trustAllCerts)
                hostnameVerifier(hostnameVerifier = { _, _ -> true })
            }
        }
    }
}