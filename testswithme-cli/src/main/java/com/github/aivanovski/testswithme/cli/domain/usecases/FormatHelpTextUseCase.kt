package com.github.aivanovski.testswithme.cli.domain.usecases

class FormatHelpTextUseCase(
    private val getVersionUseCase: GetVersionUseCase
) {

    fun formatHelpText(): String {
        return HELP_TEXT.format(
            getVersionUseCase.getVersionName()
        )
    }

    companion object {
        internal val HELP_TEXT = """
            TestsWithMe %s

            USAGE:
                tests-with-me [OPTIONS] [VALUES]

            OPTIONS:
                -w, --watch-file [FILE]          Watches for changes in the specified file
                -h, --help                       Print help information
        """.trimIndent()
    }
}