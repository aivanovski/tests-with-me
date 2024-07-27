package com.github.aivanovski.testwithme.web

import com.github.aivanovski.testwithme.web.data.database.configureDatabase
import com.github.aivanovski.testwithme.web.di.WebAppModule
import com.github.aivanovski.testwithme.web.presentation.configureAuthentication
import com.github.aivanovski.testwithme.web.presentation.routes.configureRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
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
        configureDatabase()
        configureAuthentication()
        configureRouting()
    }.start(wait = true)
}