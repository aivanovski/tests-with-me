package com.github.aivanovski.testswithme.flow.runner.reporter

import com.github.aivanovski.testswithme.flow.runner.reporter.OutputWriter.Level

class LeveledOutputWriter(
    private val source: OutputWriter
) : OutputWriter {

    fun debug(line: String) {
        source.println(Level.DEBUG, line)
    }

    fun error(line: String) {
        source.println(Level.ERROR, line)
    }

    override fun println(
        level: Level,
        line: String
    ) {
        source.println(level, line)
    }
}