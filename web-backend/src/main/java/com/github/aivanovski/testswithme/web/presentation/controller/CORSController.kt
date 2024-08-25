package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.web.entity.Response
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import org.slf4j.LoggerFactory

class CORSController {

    fun handleCorsOptionsCall(call: ApplicationCall): Either<AppException, Response<String>> =
        either {
            val headers = call.request.headers

            val origin = headers[HttpHeaders.Origin]
            val requestedHeaders = headers[HttpHeaders.AccessControlRequestHeaders]

            LOGGER.debug(
                "processCorsOptionsRequest: origin=%s, requestedHeader=%s".format(
                    origin,
                    requestedHeaders
                )
            )

            val responseHeaders = listOf(
                HttpHeaders.AccessControlAllowOrigin to (origin ?: StringUtils.STAR),
                HttpHeaders.AccessControlAllowHeaders to requestedHeaders.orEmpty(),
                HttpHeaders.AccessControlExposeHeaders to HttpHeaders.AccessControlAllowOrigin,
                HttpHeaders.AccessControlAllowCredentials to "true"
            )

            Response(
                response = StringUtils.EMPTY,
                headers = responseHeaders
            )
        }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CORSController::class.java)
    }
}