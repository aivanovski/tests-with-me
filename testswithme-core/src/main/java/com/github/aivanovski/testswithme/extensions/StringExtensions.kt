package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.entity.HashType
import com.github.aivanovski.testswithme.utils.StringUtils.NEW_LINE
import java.security.MessageDigest

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

fun String.trimLines(): String {
    return this.splitIntoLines()
        .mapNotNull { line ->
            if (line.isNotBlank()) {
                line.trim()
            } else {
                null
            }
        }
        .joinToString(separator = "\n")
}

fun String.splitToPair(separator: String): Pair<String, String>? {
    val splitIdx = this.indexOf(separator)
    if (splitIdx == -1 || splitIdx + 1 >= this.length) {
        return null
    }

    return this.substring(0, splitIdx) to this.substring(splitIdx + 1)
}

fun String.splitIntoLines(): List<String> {
    return this.split(NEW_LINE)
}

fun String.sha256(): Hash {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())

    return Hash(
        type = HashType.SHA_256,
        value = bytes.joinToString(
            separator = "",
            transform = { byte -> "%02x".format(byte) }
        )
    )
}

fun String.orNull(): String? {
    return ifEmpty { null }
}

fun String.isDigitOnly(): Boolean {
    return this.all { char -> char.isDigit() }
}

fun String.toLongSafely(): Long? {
    return try {
        java.lang.Long.parseLong(this)
    } catch (exception: NumberFormatException) {
        null
    }
}

fun String.toIntSafely(): Int? {
    return try {
        Integer.parseInt(this)
    } catch (exception: NumberFormatException) {
        null
    }
}

fun String.toBooleanSafely(): Boolean? {
    return when {
        this.equals("true", ignoreCase = true) -> true
        this.equals("false", ignoreCase = true) -> false
        else -> null
    }
}

fun String.ellipsize(
    maxLength: Int,
    ending: String
): String {
    return if (this.length > maxLength) {
        var index = maxLength

        while (this[index].isWhitespace() && index > 0) {
            index--
        }

        val substring = if (index != 0) {
            this.substring(0, index)
        } else {
            this.substring(0, maxLength)
        }

        substring + ending
    } else {
        this
    }
}