package com.github.aivanovski.testswithme.android.domain.driverServer

import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints.PORT
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import java.util.concurrent.locks.ReentrantLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okio.withLock
import timber.log.Timber

class GatewayServer {

    private val lock = ReentrantLock()

    @Volatile
    private var isRunning = false

    @Volatile
    private var server: NettyApplicationEngine? = null

    fun isRunning(): Boolean = isRunning

    fun start() {
        lock.withLock {
            if (isRunning) {
                return@withLock
            }

            Timber.d("Starting server on port $PORT...")

            isRunning = true

            server = embeddedServer(
                factory = Netty,
                port = PORT,
                module = { configureServer() }
            )
                .apply {
                    start(wait = false)
                }

            Timber.d("Server tarted")
        }
    }

    fun stop() {
        lock.withLock {
            if (!isRunning) {
                return@withLock
            }

            isRunning = false
            server?.stop()
            server = null
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun Application.configureServer() {
        install(ContentNegotiation) {
            json(
                Json {
                    explicitNulls = false
                }
            )
        }

        configureRoutes()
    }
}