package com.github.aivanovski.testswithme.flow.runner.reporter

interface OutputWriter {
    fun println(
        level: Level,
        line: String
    )

    enum class Level {
        DEBUG,
        ERROR
    }
}