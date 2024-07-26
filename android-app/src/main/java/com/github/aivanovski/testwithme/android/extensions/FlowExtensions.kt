package com.github.aivanovski.testwithme.android.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

fun <T : Any> T.asFlow(): Flow<T> =
    flowOf(this)

fun <T> Flow<T>.delayAtLeast(delayInMillis: Long): Flow<T> {
    val upstream = this

    return flow {
        delay(delayInMillis)
        emitAll(upstream)
    }
}