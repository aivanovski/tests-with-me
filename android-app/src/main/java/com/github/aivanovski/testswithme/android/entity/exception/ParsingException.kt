package com.github.aivanovski.testswithme.android.entity.exception

class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)