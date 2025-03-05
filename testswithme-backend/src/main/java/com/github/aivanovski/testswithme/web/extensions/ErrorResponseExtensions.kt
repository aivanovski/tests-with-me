package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.extensions.getRootCause
import com.github.aivanovski.testswithme.web.entity.ErrorResponse
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundException
import com.github.aivanovski.testswithme.web.entity.exception.ExpiredTokenException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidCredentialsException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidParameterException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidRequestFieldException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidTokenException
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import com.github.aivanovski.testswithme.web.entity.exception.ValidationException
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
        is ValidationException -> HttpStatusCode.BadRequest
        else -> null
    }
}

fun Exception.toErrorResponse(): ErrorResponse {
    val exception = this
    val cause = exception.getRootCause()

    return ErrorResponse(
        status = exception.toHttpStatus() ?: HttpStatusCode.BadRequest,
        exception = if (exception is AppException) {
            exception
        } else {
            AppException(cause = exception)
        },
        message = cause.message
    )
}