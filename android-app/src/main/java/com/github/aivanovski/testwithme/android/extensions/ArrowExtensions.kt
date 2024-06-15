package com.github.aivanovski.testwithme.android.extensions

import arrow.core.Either

fun <E, V> Either<E, V>.unwrap(): V {
    val value = getOrNull()
    requireNotNull(value)
    return value
}

fun <E, V> Either<E, V>.unwrapNullable(): V? {
    return getOrNull()
}

fun <E, V> Either<E, V>.unwrapError(): E {
    val error = leftOrNull()
    requireNotNull(error)
    return error
}