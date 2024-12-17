package com.github.aivanovski.testswithme.android.entity.exception

import io.ktor.http.HttpStatusCode
import java.io.IOException

open class ApiException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

open class NetworkException(
    cause: IOException
) : ApiException(cause = cause)

class InvalidHttpStatusCodeException(
    status: HttpStatusCode
) : ApiException(message = "Invalid HTTP status code: %s".format(status))

class NoAccountDataException : ApiException(
    message = "No account data found"
)