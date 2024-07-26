package com.github.aivanovski.testwithme.web.extensions

import arrow.core.Either
import com.github.aivanovski.testwithme.web.entity.ErrorResponse
import com.github.aivanovski.testwithme.web.entity.exception.AppException

fun <T> Either<AppException, T>.transformError(): Either<ErrorResponse, T> {
    return this.mapLeft { exception -> exception.toErrorResponse() }
}