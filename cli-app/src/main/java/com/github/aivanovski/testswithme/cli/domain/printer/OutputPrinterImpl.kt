package com.github.aivanovski.testswithme.cli.domain.printer

class OutputPrinterImpl : OutputPrinter {

    @Volatile
    private var currentLevel: OutputLevel = OutputLevel.OUTPUT

    override fun setOutputLevel(level: OutputLevel) {
        this.currentLevel = level
    }

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
        if (level.value <= currentLevel.value) {
            if (currentLevel == OutputLevel.DEBUG) {
                println("[$level] $line")
            } else {
                println(line)
            }
        }
    }
}