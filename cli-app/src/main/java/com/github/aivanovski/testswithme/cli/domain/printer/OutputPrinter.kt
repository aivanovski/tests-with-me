package com.github.aivanovski.testswithme.cli.domain.printer

interface OutputPrinter {
    fun printLine(line: String)
    fun debugLine(line: String)
}