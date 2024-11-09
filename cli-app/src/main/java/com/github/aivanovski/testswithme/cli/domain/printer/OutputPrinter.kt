package com.github.aivanovski.testswithme.cli.domain.printer

interface OutputPrinter {
    fun setOutputLevel(level: OutputLevel)
    fun printLine(line: String)
    fun debugLine(line: String)
}