package com.github.aivanovski.testswithme.web.extensions

import arrow.core.Either
import com.github.aivanovski.testswithme.web.entity.ErrorResponse
import com.github.aivanovski.testswithme.web.entity.exception.AppException

fun <T> Either<AppException, T>.transformError(): Either<ErrorResponse, T> {
    return this.mapLeft { exception -> exception.toErrorResponse() }
}