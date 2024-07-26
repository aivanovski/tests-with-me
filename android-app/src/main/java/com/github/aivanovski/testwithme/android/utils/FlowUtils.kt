package com.github.aivanovski.testwithme.android.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun infiniteRepeatFlow(delay: Duration): Flow<Unit> {
    return flow {
        while (true) {
            delay(delay)
            emit(Unit)
        }
    }
}