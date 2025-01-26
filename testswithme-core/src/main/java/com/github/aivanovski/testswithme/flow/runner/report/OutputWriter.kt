package com.github.aivanovski.testswithme.flow.runner.report

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