package com.github.aivanovski.testswithme.android.utils

import kotlin.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun infiniteRepeatFlow(delay: Duration): Flow<Unit> {
    return flow {
        while (true) {
            delay(delay)
            emit(Unit)
        }
    }
}