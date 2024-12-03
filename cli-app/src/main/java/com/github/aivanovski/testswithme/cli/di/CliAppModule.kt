package com.github.aivanovski.testswithme.cli.di

import com.github.aivanovski.testswithme.cli.data.argument.ArgumentParser
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProviderImpl
import com.github.aivanovski.testswithme.cli.data.network.GatewayClient
import com.github.aivanovski.testswithme.cli.domain.usecases.ConnectToDeviceUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.FormatHelpTextUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.GetVersionUseCase
import com.github.aivanovski.testswithme.cli.presentation.core.CliStrings
import com.github.aivanovski.testswithme.cli.presentation.core.CliStringsImpl
import com.github.aivanovski.testswithme.cli.presentation.main.MainInteractor
import com.github.aivanovski.testswithme.cli.presentation.main.MainViewModel
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object CliAppModule {

    val module = module {
        single<FileSystemProvider> { FileSystemProviderImpl() }
        single<CliStrings> { CliStringsImpl() }
        singleOf(::JsonSerializer)
        singleOf(::GatewayClient)
        singleOf(::ArgumentParser)

        // Use cases
        singleOf(::GetVersionUseCase)
        singleOf(::FormatHelpTextUseCase)
        singleOf(::ConnectToDeviceUseCase)

        singleOf(::MainInteractor)
        singleOf(::MainViewModel)
    }
}