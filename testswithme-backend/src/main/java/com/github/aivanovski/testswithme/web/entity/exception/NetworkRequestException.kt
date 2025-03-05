package com.github.aivanovski.testswithme.web.entity.exception

class NetworkRequestException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause)