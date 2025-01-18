package com.github.aivanovski.testswithme.android.entity.exception

open class GatewayException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class InvalidParameterException(
    parameterName: String
) : GatewayException(message = "Invalid parameter: %s".format(parameterName))