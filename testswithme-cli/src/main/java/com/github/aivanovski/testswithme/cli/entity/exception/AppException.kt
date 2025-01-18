package com.github.aivanovski.testswithme.cli.entity.exception

open class AppException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)