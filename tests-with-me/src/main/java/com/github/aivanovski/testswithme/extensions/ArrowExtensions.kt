package com.github.aivanovski.testswithme.extensions

import arrow.core.Either

fun <E, V : Any?> Either<E, V>.unwrap(): V {
    return getOrNull() as V
}

fun <E, V> Either<E, V>.unwrapError(): E {
    val error = leftOrNull()
    requireNotNull(error)
    return error
}

fun <E : Exception, V> Either<E, V>.unwrapOrReport(): V {
    return if (isRight()) {
        unwrap()
    } else {
        throw unwrapError()
    }
}