package com.github.aivanovski.testswithme.cli

import com.github.aivanovski.testswithme.cli.di.CliAppModule
import com.github.aivanovski.testswithme.cli.di.GlobalInjector.get
import com.github.aivanovski.testswithme.cli.presentation.core.SystemErrorToLoggerOutputStream
import com.github.aivanovski.testswithme.cli.presentation.main.MainView
import com.github.aivanovski.testswithme.cli.presentation.main.MainViewModel
import java.io.PrintStream
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    startKoin {
        modules(CliAppModule.module)
    }

    val viewModel: MainViewModel = get()
    val view = MainView(get())

    val errorLogger = LoggerFactory.getLogger("SystemErr")
    System.setErr(PrintStream(SystemErrorToLoggerOutputStream(errorLogger)))

    view.render(viewModel)
    viewModel.start(args)
    view.stop()
}