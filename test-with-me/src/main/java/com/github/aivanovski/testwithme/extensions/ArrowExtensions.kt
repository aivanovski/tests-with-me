package com.github.aivanovski.testwithme.extensions

import arrow.core.Either

fun <E, V : Any?> Either<E, V>.unwrap(): V {
    return getOrNull() as V
}

fun <E, V> Either<E, V>.unwrapError(): E {
    val error = leftOrNull()
    requireNotNull(error)
    return error
}