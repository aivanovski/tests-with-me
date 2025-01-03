package com.github.aivanovski.testswithme.web

import com.github.aivanovski.testswithme.extensions.unwrapOrReport
import com.github.aivanovski.testswithme.web.data.arguments.ArgumentParser
import com.github.aivanovski.testswithme.web.data.database.configureDatabase
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.di.WebAppModule
import com.github.aivanovski.testswithme.web.domain.usecases.GetSslKeyStoreUseCase
import com.github.aivanovski.testswithme.web.entity.NetworkProtocolType
import com.github.aivanovski.testswithme.web.presentation.configureAuthentication
import com.github.aivanovski.testswithme.web.presentation.routes.configureRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(WebAppModule.module)
    }

    val getKeyStoreUseCase: GetSslKeyStoreUseCase = get()
    val argumentParser: ArgumentParser = get()

    val arguments = argumentParser.parse(args).unwrapOrReport()

    val environment = applicationEngineEnvironment {
        when (arguments.protocolType) {
            NetworkProtocolType.HTTP -> {
                connector {
                    port = 8080
                }
            }

            NetworkProtocolType.HTTPS -> {
                val keyStore = getKeyStoreUseCase.getKeyStore().unwrapOrReport()

                sslConnector(
                    keyStore = keyStore.keyStore,
                    keyAlias = keyStore.alias,
                    keyStorePassword = { keyStore.password.toCharArray() },
                    privateKeyPassword = { keyStore.password.toCharArray() }
                ) {
                    port = 8443
                }
            }
        }

        module(Application::appModule)
    }

    embeddedServer(Netty, environment).start(wait = true)
}

@OptIn(ExperimentalSerializationApi::class)
private fun Application.appModule() {
    install(ContentNegotiation) {
        json(
            Json {
                explicitNulls = false
            }
        )
    }
    configureDatabase()
    configureAuthentication()
    configureRoutes()
}