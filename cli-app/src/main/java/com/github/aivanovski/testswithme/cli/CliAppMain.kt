package com.github.aivanovski.testswithme.cli

import com.github.aivanovski.testswithme.cli.di.CliAppModule
import com.github.aivanovski.testswithme.cli.di.GlobalInjector.get
import com.github.aivanovski.testswithme.cli.domain.MainInteractor
import com.github.aivanovski.testswithme.extensions.unwrapOrReport
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(CliAppModule.module)
    }

    val interactor: MainInteractor = get()

    runBlocking {
        val result = interactor.process(args)
        result.unwrapOrReport()
    }
}