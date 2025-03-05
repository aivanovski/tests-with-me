package com.github.aivanovski.testswithme.web.presentation

import arrow.core.Either
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.web.api.response.ErrorMessage
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.entity.ErrorResponse
import com.github.aivanovski.testswithme.web.entity.Response
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.extensions.transformError
import com.github.aivanovski.testswithme.web.presentation.Errors.ERROR_HAS_BEEN_OCCURRED
import com.github.aivanovski.testswithme.web.presentation.Errors.INVALID_OR_EXPIRED_TOKEN
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val responseLogger: Logger = LoggerFactory.getLogger("ResponseHandler")

suspend inline fun <reified T : Any> handleResponseWithUser(
    authService: AuthService,
    call: ApplicationCall,
    block: (user: User) -> Either<AppException, T>
) {
    val principal = call.principal<JWTPrincipal>()
    if (principal == null) {
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = ErrorMessage(message = INVALID_OR_EXPIRED_TOKEN)
        )
        return
    }

    val isValidTokenResult = authService.validateToken(principal)
    if (isValidTokenResult.isLeft()) {
        call.sendResponse(isValidTokenResult)
        return
    }

    val user = isValidTokenResult.unwrap()

    val response = block.invoke(user)
        .transformError()

    call.sendResponse(response)
}

suspend inline fun <reified T : Any> handleResponse(
    call: ApplicationCall,
    block: () -> Either<AppException, T>
) {
    val response = block.invoke()
        .transformError()

    call.sendResponse(response)
}

suspend inline fun <reified T : Any> ApplicationCall.sendResponse(
    response: Either<ErrorResponse, T>
) {
    if (response.isRight()) {
        responseLogger.debug(formatRequestLogMessage(response))
    } else {
        responseLogger.error(formatRequestLogMessage(response), response.unwrapError().exception)
    }

    if (response.isRight()) {
        val originHeader = request.headers[HttpHeaders.Origin]

        if (!originHeader.isNullOrBlank()) {
            this.response.headers.apply {
                append(HttpHeaders.AccessControlAllowOrigin, originHeader)
                append(HttpHeaders.AccessControlAllowCredentials, "true")
                append(HttpHeaders.AccessControlExposeHeaders, HttpHeaders.AccessControlAllowOrigin)
            }
        }

        @Suppress("UNCHECKED_CAST")
        val responseWrapper = response.unwrap() as? Response<T>

        if (responseWrapper != null) {
            for ((name, value) in responseWrapper.headers) {
                this.response.headers.append(name, value)
            }

            respond(
                status = HttpStatusCode.OK,
                message = responseWrapper.response
            )
        } else {
            respond(
                status = HttpStatusCode.OK,
                message = response.unwrap()
            )
        }
    } else {
        val error = response.unwrapError()

        respond(
            status = error.status,
            message = error.toErrorMessage()
        )
    }
}

fun ApplicationCall.formatRequestLogMessage(response: Either<ErrorResponse, Any>): String {
    return when (response) {
        is Either.Left -> {
            val error = response.unwrapError()

            "Request: %s, FAILURE, status=%s, message=%s".format(
                request.uri,
                error.status,
                error.message
            )
        }

        is Either.Right -> {
            "Request: %s, SUCCESS".format(request.uri)
        }
    }
}

fun ErrorResponse.toErrorMessage(): ErrorMessage {
    return ErrorMessage(
        message = message ?: ERROR_HAS_BEEN_OCCURRED
    )
}