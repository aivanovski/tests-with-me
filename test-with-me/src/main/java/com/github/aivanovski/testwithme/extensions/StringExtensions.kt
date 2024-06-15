package com.github.aivanovski.testwithme.extensions

fun String.isDigitOnly(): Boolean {
    return this.all { char -> char.isDigit() }
}

fun String.toLongSafely(): Long? {
    return try {
        java.lang.Long.valueOf(this)
    } catch (exception: NumberFormatException) {
        null
    }
}