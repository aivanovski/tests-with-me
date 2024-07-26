package com.github.aivanovski.testwithme.extensions

import java.io.PrintWriter
import java.io.StringWriter

fun Exception.printStackTraceToString(): String {
    val writer = StringWriter()
    val printer = PrintWriter(writer)
    this.printStackTrace(printer)
    return writer.buffer.toString()
}

fun Exception.getRootCause(): Throwable {
    var current: Throwable = this

    while (current.cause != null) {
        val cause = current.cause
        requireNotNull(cause)
        current = cause
    }

    return current
}