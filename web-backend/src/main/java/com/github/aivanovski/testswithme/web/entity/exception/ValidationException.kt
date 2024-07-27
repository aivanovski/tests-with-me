package com.github.aivanovski.testswithme.web.entity.exception

open class ValidationException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)