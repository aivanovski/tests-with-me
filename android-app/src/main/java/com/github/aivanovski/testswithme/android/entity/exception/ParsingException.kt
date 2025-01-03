package com.github.aivanovski.testswithme.android.entity.exception

open class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class InvalidBase64StringException : ParsingException(
    message = "Invalid Base64 string"
)