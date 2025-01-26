package com.github.aivanovski.testswithme.utils

import com.github.aivanovski.testswithme.flow.runner.report.OutputWriter
import com.github.aivanovski.testswithme.flow.runner.report.OutputWriter.Level

interface Logger : OutputWriter {
    fun debug(message: String) {
        println(Level.DEBUG, message)
    }

    fun error(message: String) {
        println(Level.ERROR, message)
    }
}