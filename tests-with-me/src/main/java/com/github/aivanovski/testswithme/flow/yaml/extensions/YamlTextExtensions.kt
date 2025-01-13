package com.github.aivanovski.testswithme.flow.yaml.extensions

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.flow.yaml.model.TextLine
import com.github.aivanovski.testswithme.flow.yaml.model.TextLineRange
import com.github.aivanovski.testswithme.flow.yaml.model.exception.InvalidKeyException
import com.github.aivanovski.testswithme.flow.yaml.model.exception.InvalidLineFormatException
import com.github.aivanovski.testswithme.flow.yaml.model.exception.MissingCharacterException
import com.github.aivanovski.testswithme.flow.yaml.model.exception.YamlParsingException

private const val HYPHEN = '-'
private const val COLON = ':'
private const val WHITESPACE = ' '

fun TextLine.isBlank(): Boolean = text.isBlank()

fun TextLineRange.format(): String {
    return lines.joinToString(
        separator = "\n",
        transform = { line -> line.text }
    )
}

fun TextLine.keySubstring(): Either<YamlParsingException, String> {
    val line = this
    return either {
        if (line.isBlank()) {
            raise(InvalidLineFormatException(line))
        }

        val textStartIndex = line.text.indexOfFirst { char -> char != WHITESPACE }

        val keyStartIndex = if (line.text[textStartIndex] == HYPHEN) {
            if (line.text.getOrNull(textStartIndex + 1) != WHITESPACE) {
                raise(MissingCharacterException(WHITESPACE, line))
            }

            textStartIndex + 2
        } else {
            textStartIndex
        }

        val colonIndex = line.text.indexOf(COLON)
        if (colonIndex == -1) {
            raise(MissingCharacterException(COLON, line))
        }

        if (keyStartIndex >= colonIndex) {
            raise(InvalidLineFormatException(line))
        }

        val key = line.text.substring(keyStartIndex, colonIndex)
        if (key.isBlank()) {
            raise(InvalidKeyException(line))
        }

        key.trim()
    }
}

fun TextLine.valueSubstring(): Either<YamlParsingException, String> {
    val line = this

    return either {
        val colonIndex = line.text.indexOf(COLON)
        if (colonIndex == -1) {
            raise(MissingCharacterException(COLON, line))
        }

        line.text.substringAfter(COLON).trim()
    }
}