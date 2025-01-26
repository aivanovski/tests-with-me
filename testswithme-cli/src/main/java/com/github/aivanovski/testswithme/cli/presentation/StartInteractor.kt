package com.github.aivanovski.testswithme.cli.presentation

import com.github.aivanovski.testswithme.cli.data.argument.ArgumentParser
import com.github.aivanovski.testswithme.cli.di.GlobalInjector
import com.github.aivanovski.testswithme.cli.domain.usecases.FormatHelpTextUseCase
import com.github.aivanovski.testswithme.cli.presentation.core.SystemErrorToLoggerOutputStream
import com.github.aivanovski.testswithme.cli.presentation.main.MainView
import com.github.aivanovski.testswithme.cli.presentation.main.MainViewModel
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapOrReport
import java.io.PrintStream
import kotlin.system.exitProcess
import org.koin.core.parameter.parametersOf
import org.slf4j.LoggerFactory

class StartInteractor(
    private val formatHelpUseCase: FormatHelpTextUseCase,
    private val argumentParser: ArgumentParser
) {

    fun startApp(args: List<String>) {
        val parseArgumentsResult = argumentParser.parse(args)
        if (parseArgumentsResult.isLeft()) {
            parseArgumentsResult.unwrapOrReport()
            return
        }

        val arguments = parseArgumentsResult.unwrap()
        if (arguments.isPrintHelp) {
            println(formatHelpUseCase.formatHelpText())
            return
        }

        val viewModel = GlobalInjector.get<MainViewModel>(params = parametersOf(arguments))
        val view = MainView(GlobalInjector.get())

        val errorLogger = LoggerFactory.getLogger("SystemErr")
        System.setErr(PrintStream(SystemErrorToLoggerOutputStream(errorLogger)))

        view.bind(viewModel)
        viewModel.start()
        view.unbind()

        exitProcess(0)
    }
}