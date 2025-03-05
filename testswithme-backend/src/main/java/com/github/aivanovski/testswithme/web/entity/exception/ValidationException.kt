package com.github.aivanovski.testswithme.web.entity.exception

open class ValidationException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause)