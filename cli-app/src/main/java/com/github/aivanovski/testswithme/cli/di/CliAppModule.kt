package com.github.aivanovski.testswithme.cli.di

import com.github.aivanovski.testswithme.cli.data.argument.ArgumentParser
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProviderImpl
import com.github.aivanovski.testswithme.cli.data.network.GatewayClient
import com.github.aivanovski.testswithme.cli.data.network.HttpRequestExecutor
import com.github.aivanovski.testswithme.cli.domain.MainInteractor
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinterImpl
import com.github.aivanovski.testswithme.cli.domain.usecases.ConnectToDeviceUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.GetVersionUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.PrintHelpUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object CliAppModule {

    val module = module {
        single<OutputPrinter> { OutputPrinterImpl() }
        single<FileSystemProvider> { FileSystemProviderImpl() }
        single<HttpClient> { provideHttpClient() }
        singleOf(::HttpRequestExecutor)
        singleOf(::GatewayClient)
        singleOf(::ArgumentParser)

        // Gateway Server

        // Use cases
        singleOf(::GetVersionUseCase)
        singleOf(::PrintHelpUseCase)
        singleOf(::ConnectToDeviceUseCase)

        singleOf(::MainInteractor)
    }

    private fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.NONE
            }
        }
    }
}