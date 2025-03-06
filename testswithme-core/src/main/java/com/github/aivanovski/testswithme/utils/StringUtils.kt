package com.github.aivanovski.testswithme.utils

object StringUtils {
    const val EMPTY = ""
    const val SPACE = " "
    const val SPACE_CHAR = ' '
    const val NEW_LINE = "\n"
    const val TAB = "\t"
    const val DOTS = "…"
    const val SLASH = "/"
    const val DASH = "-"
    const val STAR = "*"
    const val SEMICOLON = ":"

    fun concatPaths(vararg paths: String): String {
        val sb = StringBuilder(paths.first().removeSuffix("/"))

        for (index in 1 until paths.size) {
            sb.append("/").append(paths[index].removePrefix("/"))
        }

        return sb.toString()
    }
}