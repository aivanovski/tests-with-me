package com.github.aivanovski.testswithme.cli

import com.github.aivanovski.testswithme.cli.di.CliAppModule
import com.github.aivanovski.testswithme.cli.di.GlobalInjector.get
import com.github.aivanovski.testswithme.cli.presentation.StartInteractor
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(CliAppModule.module)
    }

    val interactor: StartInteractor = get()

    interactor.startApp(args.toList())
}