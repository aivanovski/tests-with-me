package com.github.aivanovski.testswithme.cli.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.argument.ArgumentParser
import com.github.aivanovski.testswithme.cli.data.argument.OptionType
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.domain.printer.OutputLevel
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.domain.usecases.ConnectToDeviceUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.PrintHelpUseCase
import com.github.aivanovski.testswithme.cli.entity.exception.AppException

class MainInteractor(
    private val fsProvider: FileSystemProvider,
    private val printHelpUseCase: PrintHelpUseCase,
    private val connectUseCase: ConnectToDeviceUseCase,
    private val argumentParser: ArgumentParser,
    private val printer: OutputPrinter
) {

    suspend fun process(args: Array<String>): Either<AppException, Unit> =
        either {
            if (args.contains(OptionType.DEBUG.shortName) ||
                args.contains(OptionType.DEBUG.fullName)
            ) {
                OutputLevel.setOutputLevel(OutputLevel.DEBUG)
            }

            if (args.isEmpty()) {
                printHelpUseCase.printHelp()
                return@either
            }
            val arguments = argumentParser.parse(args).bind()

            if (arguments.isPrintHelp) {
                printHelpUseCase.printHelp()
                return@either
            }

            val connection = connectUseCase.connectToDevice(printer).bind()

            EventLoop(
                fsProvider = fsProvider,
                printer = printer,
                connection = connection
            ).loop(arguments.filePath)
        }
}