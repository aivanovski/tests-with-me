package com.github.aivanovski.testswithme.web.entity

enum class CliArgument(
    val shortName: String,
    val longName: String
) {
    PROTOCOL(
        shortName = "-p",
        longName = "--protocol"
    )
}