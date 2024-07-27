package com.github.aivanovski.testwithme.android.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun <T : Any> T.asFlow(): Flow<T> = flowOf(this)