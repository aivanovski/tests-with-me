package com.github.aivanovski.testswithme.web.presentation.controller

import com.github.aivanovski.testswithme.utils.StringUtils
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.slf4j.LoggerFactory

class CORSController {

    suspend fun handleCorsOptionsCall(
        call: ApplicationCall
    ) {
        val headers = call.request.headers

        val origin = headers[HttpHeaders.Origin]
        val requestedHeaders = headers[HttpHeaders.AccessControlRequestHeaders]

        LOGGER.debug(
            "processCorsOptionsRequest: origin=%s, requestedHeader=%s".format(
                origin,
                requestedHeaders
            )
        )

        call.response.headers.apply {
            append(HttpHeaders.AccessControlAllowOrigin, origin ?: StringUtils.STAR)
            append(
                HttpHeaders.AccessControlAllowHeaders,
                requestedHeaders ?: StringUtils.EMPTY
            )
            append(
                HttpHeaders.AccessControlExposeHeaders,
                HttpHeaders.AccessControlAllowOrigin
            )
            append(HttpHeaders.AccessControlAllowCredentials, "true")
        }

        call.respond(HttpStatusCode.OK, StringUtils.EMPTY)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CORSController::class.java)
    }
}