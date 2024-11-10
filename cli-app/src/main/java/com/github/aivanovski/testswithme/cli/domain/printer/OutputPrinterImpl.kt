package com.github.aivanovski.testswithme.cli.domain.printer

class OutputPrinterImpl : OutputPrinter {

    override fun printLine(line: String) {
        appendLine(OutputLevel.OUTPUT, line)
    }

    override fun debugLine(line: String) {
        appendLine(OutputLevel.DEBUG, line)
    }

    private fun appendLine(
        level: OutputLevel,
        line: String
    ) {
        val currentLevel = OutputLevel.getOutputLevel()

        if (level.value <= currentLevel.value) {
            if (currentLevel == OutputLevel.DEBUG) {
                println("[$level] $line")
            } else {
                println(line)
            }
        }
    }
}