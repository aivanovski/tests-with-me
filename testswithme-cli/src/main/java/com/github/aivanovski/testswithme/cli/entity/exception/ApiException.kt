package com.github.aivanovski.testswithme.cli.entity.exception

import io.ktor.http.HttpStatusCode

open class ApiException(
    message: String? = null,
    cause: Exception? = null
) : DeviceConnectionException(message, cause)

open class NetworkException(
    message: String? = null,
    cause: Exception? = null
) : ApiException(message, cause)

class InvalidHttpStatusCodeException(
    status: HttpStatusCode
) : ApiException(message = "Invalid HTTP status code: %s".format(status))

class FailedToConnectToGatewayServer : NetworkException("Failed connect to Gateway HTTP server")