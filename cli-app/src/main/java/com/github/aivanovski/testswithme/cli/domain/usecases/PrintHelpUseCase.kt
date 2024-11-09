package com.github.aivanovski.testswithme.cli.domain.usecases

import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter

class PrintHelpUseCase(
    private val getVersionUseCase: GetVersionUseCase,
    private val printer: OutputPrinter
) {

    fun printHelp() {
        printer.printLine(
            HELP_TEXT.format(
                getVersionUseCase.getVersionName()
            )
        )
    }

    companion object {
        internal val HELP_TEXT = """
            TestsWithMe %s

            USAGE:
                tests-with-me [OPTIONS] [VALUES]

            OPTIONS:
                -w, --watch-file [FILE]          Watches for changes in the specifed file
                -d, --debug                      Print debug information to output
                -h, --help                       Print help information
        """.trimIndent()
    }
}