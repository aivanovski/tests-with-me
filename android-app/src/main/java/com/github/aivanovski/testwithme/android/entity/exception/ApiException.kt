package com.github.aivanovski.testwithme.android.entity.exception

import io.ktor.http.HttpStatusCode

open class ApiException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class InvalidHttpStatusCodeException(
    status: HttpStatusCode
) : ApiException(message = "Invalid HTTP status code: %s".format(status))