package com.github.aivanovski.testswithme.android.utils

import arrow.core.Either
import com.github.aivanovski.testswithme.extensions.remapError
import com.github.aivanovski.testswithme.extensions.unwrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun <E, T1, T2, R> combineEitherFlows(
    flow1: Flow<Either<E, T1>>,
    flow2: Flow<Either<E, T2>>,
    transform: suspend (T1, T2) -> Either<E, R>
): Flow<Either<E, R>> =
    combine(flow1, flow2) { e1, e2 ->
        if (e1.isLeft()) return@combine e1.remapError()
        if (e2.isLeft()) return@combine e2.remapError()

        transform.invoke(e1.unwrap(), e2.unwrap())
    }

fun <E, T1, T2, T3, R> combineEitherFlows(
    flow1: Flow<Either<E, T1>>,
    flow2: Flow<Either<E, T2>>,
    flow3: Flow<Either<E, T3>>,
    transform: suspend (T1, T2, T3) -> Either<E, R>
): Flow<Either<E, R>> =
    combine(flow1, flow2, flow3) { e1, e2, e3 ->
        if (e1.isLeft()) return@combine e1.remapError()
        if (e2.isLeft()) return@combine e2.remapError()
        if (e3.isLeft()) return@combine e3.remapError()

        transform.invoke(e1.unwrap(), e2.unwrap(), e3.unwrap())
    }

fun <E, T1, T2, T3, T4, T5, R> combineEitherFlows(
    flow1: Flow<Either<E, T1>>,
    flow2: Flow<Either<E, T2>>,
    flow3: Flow<Either<E, T3>>,
    flow4: Flow<Either<E, T4>>,
    flow5: Flow<Either<E, T5>>,
    transform: suspend (T1, T2, T3, T4, T5) -> Either<E, R>
): Flow<Either<E, R>> =
    combine(flow1, flow2, flow3, flow4, flow5) { e1, e2, e3, e4, e5 ->
        if (e1.isLeft()) return@combine e1.remapError()
        if (e2.isLeft()) return@combine e2.remapError()
        if (e3.isLeft()) return@combine e3.remapError()
        if (e4.isLeft()) return@combine e4.remapError()
        if (e5.isLeft()) return@combine e5.remapError()

        transform.invoke(e1.unwrap(), e2.unwrap(), e3.unwrap(), e4.unwrap(), e5.unwrap())
    }

fun <E, T1, T2, T3, T4, T5, T6, R> combineEitherFlows(
    flow1: Flow<Either<E, T1>>,
    flow2: Flow<Either<E, T2>>,
    flow3: Flow<Either<E, T3>>,
    flow4: Flow<Either<E, T4>>,
    flow5: Flow<Either<E, T5>>,
    flow6: Flow<Either<E, T6>>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> Either<E, R>
): Flow<Either<E, R>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6) { arr ->
        for (e in arr) {
            if (e.isLeft()) return@combine e.remapError()
        }

        transform.invoke(
            arr[0].unwrap() as T1,
            arr[1].unwrap() as T2,
            arr[2].unwrap() as T3,
            arr[3].unwrap() as T4,
            arr[4].unwrap() as T5,
            arr[5].unwrap() as T6
        )
    }