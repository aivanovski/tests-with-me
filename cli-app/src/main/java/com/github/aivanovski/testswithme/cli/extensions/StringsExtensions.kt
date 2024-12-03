package com.github.aivanovski.testswithme.cli.extensions

import com.github.aivanovski.testswithme.utils.StringUtils.NEW_LINE

fun String.appendIndent(indent: String): String {
    return this.split(NEW_LINE)
        .joinToString(
            separator = NEW_LINE,
            transform = { line ->
                if (line.isNotEmpty()) {
                    "$indent$line"
                } else {
                    line
                }
            }
        )
}

fun String.splitIntoLines(): List<String> {
    return this.split(NEW_LINE)
}