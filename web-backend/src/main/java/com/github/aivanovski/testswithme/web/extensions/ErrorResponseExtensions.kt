package com.github.aivanovski.testswithme.web.extensions

import arrow.core.Either
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.web.entity.ErrorResponse
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundException
import com.github.aivanovski.testswithme.web.entity.exception.ExpiredTokenException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidCredentialsException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidRequestFieldException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidTokenException
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import io.ktor.http.HttpStatusCode
import kotlin.Exception

fun Exception.toHttpStatus(): HttpStatusCode? {
    return when (this) {
        is InvalidTokenException -> HttpStatusCode.Unauthorized
        is ExpiredTokenException -> HttpStatusCode.Unauthorized
        is InvalidCredentialsException -> HttpStatusCode.Unauthorized
        is InvalidParameterException -> HttpStatusCode.BadRequest
        is InvalidRequestFieldException -> HttpStatusCode.BadRequest
        is EntityNotFoundException -> HttpStatusCode.NotFound
        is ParsingException -> HttpStatusCode.BadRequest
        else -> null
    }
}

fun Exception.toErrorResponse(): ErrorResponse {
    val exception = this

    return ErrorResponse(
        status = exception.toHttpStatus() ?: HttpStatusCode.BadRequest,
        exception = if (exception is AppException) {
            exception
        } else {
            AppException(cause = exception)
        },
        message = if (exception is AppException) {
            exception.message
        } else {
            null
        }
    )
}

fun <Value> Either<Exception, Value>.toErrorResponse(): Either.Left<ErrorResponse> {
    return Either.Left(this.unwrapError().toErrorResponse())
}