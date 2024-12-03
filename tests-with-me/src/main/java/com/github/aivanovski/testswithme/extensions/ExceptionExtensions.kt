package com.github.aivanovski.testswithme.extensions

fun Throwable.getRootCause(): Throwable {
    var current: Throwable = this

    while (current.cause != null) {
        val cause = current.cause
        requireNotNull(cause)
        current = cause
    }

    return current
}