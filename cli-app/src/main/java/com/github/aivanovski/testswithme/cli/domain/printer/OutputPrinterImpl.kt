package com.github.aivanovski.testswithme.cli.domain.printer

class OutputPrinterImpl : OutputPrinter {
    override fun printLine(line: String) {
        println(line)
    }
}