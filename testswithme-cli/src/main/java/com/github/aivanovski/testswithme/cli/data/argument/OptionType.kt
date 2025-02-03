package com.github.aivanovski.testswithme.cli.data.argument

enum class OptionType(
    val shortName: String,
    val fullName: String
) {
    WATCH_FILE(shortName = "-w", fullName = "--watch-file"),
    SCREEN_SIZE(shortName = "-s", fullName = "--screen-size"),
    HELP(shortName = "-h", fullName = "--help")
}