package com.github.aivanovski.testswithme.web

import com.github.aivanovski.testswithme.web.data.database.configureDatabase
import com.github.aivanovski.testswithme.web.di.WebAppModule
import com.github.aivanovski.testswithme.web.presentation.configureAuthentication
import com.github.aivanovski.testswithme.web.presentation.routes.configureRouting
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    startKoin {
        modules(WebAppModule.module)
    }

    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json(
                Json {
                    explicitNulls = false
                }
            )
        }
        install(CORS) {
            anyHost()

            allowMethod(HttpMethod.Options)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)

            exposeHeader(HttpHeaders.AccessControlAllowOrigin)
        }
        configureDatabase()
        configureAuthentication()
        configureRouting()
    }.start(wait = true)
}