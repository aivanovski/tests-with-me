package com.github.aivanovski.testswithme.cli.di

import com.github.aivanovski.testswithme.cli.data.argument.ArgumentParser
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProviderImpl
import com.github.aivanovski.testswithme.cli.data.process.ProcessExecutor
import com.github.aivanovski.testswithme.cli.domain.FileWatcher
import com.github.aivanovski.testswithme.cli.domain.FileWatcherImpl
import com.github.aivanovski.testswithme.cli.domain.MainInteractor
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinterImpl
import com.github.aivanovski.testswithme.cli.domain.usecases.GetVersionUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.ConnectToDeviceUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.PrintHelpUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object CliAppModule {

    val module = module {
        single<OutputPrinter> { OutputPrinterImpl() }
        single<FileSystemProvider> { FileSystemProviderImpl() }
        single<FileWatcher> { FileWatcherImpl(get()) }
        singleOf(::ProcessExecutor)
        singleOf(::ArgumentParser)

        // Use cases
        singleOf(::GetVersionUseCase)
        singleOf(::PrintHelpUseCase)
        singleOf(::ConnectToDeviceUseCase)

        singleOf(::MainInteractor)
    }
}