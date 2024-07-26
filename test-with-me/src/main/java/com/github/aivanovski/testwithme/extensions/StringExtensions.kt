package com.github.aivanovski.testwithme.extensions

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

fun String.ellipsize(
    maxLength: Int,
    ending: String
): String {
    return if (this.length > maxLength) {
        val sub = this.substring(0, maxLength)
        sub + ending
    } else {
        this
    }
}