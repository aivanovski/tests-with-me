package com.github.aivanovski.testswithme.cli.data.argument

import com.github.aivanovski.testswithme.utils.StringUtils

data class Arguments(
    val filePath: String,
    val isPrintHelp: Boolean
) {
    companion object {
        val EMPTY = Arguments(
            filePath = StringUtils.EMPTY,
            isPrintHelp = false
        )
    }
}