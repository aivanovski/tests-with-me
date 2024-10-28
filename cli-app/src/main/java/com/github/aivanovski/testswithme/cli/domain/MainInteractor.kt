package com.github.aivanovski.testswithme.cli.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.adb.command.CheckApplicationInstalledCommand
import com.github.aivanovski.testswithme.cli.data.argument.ArgumentParser
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.domain.usecases.ConnectToDeviceUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.PrintHelpUseCase
import com.github.aivanovski.testswithme.cli.entity.exception.AppException
import kotlin.io.path.Path

class MainInteractor(
    private val printHelpUseCase: PrintHelpUseCase,
    private val connectUseCase: ConnectToDeviceUseCase,
    private val argumentParser: ArgumentParser,
    private val watcher: FileWatcher,
    private val printer: OutputPrinter
) {

    fun process(args: Array<String>): Either<AppException, Unit> =
        either {
            if (args.isEmpty()) {
                printHelpUseCase.printHelp()
                return@either
            }
            val arguments = argumentParser.parse(args).bind()

            if (arguments.isPrintHelp) {
                printHelpUseCase.printHelp()
                return@either
            }

            val context = connectUseCase.connectToDevice().bind()

            val isApplicationInstalled = context.execute(
                CheckApplicationInstalledCommand(packageName = APPLICATION_PACKAGE_NAME)
            ).bind()

            if (isApplicationInstalled) {
                raise(
                    AppException(
                        "TestWithMe Android application is not installed on connected device"
                    )
                )
            }

            val file = Path(arguments.filePath)
            printer.printLine("Watching file: ${arguments.filePath}")
            printer.printLine("isApplicationInstalled=$isApplicationInstalled")

//            watcher.watch(
//                file = file,
//                onContentChanged = { content ->
//                }
//            )

//            context.execute()


        }

    companion object {
        private const val APPLICATION_PACKAGE_NAME = "com.github.aivanovski.testswithme.android"
    }
}