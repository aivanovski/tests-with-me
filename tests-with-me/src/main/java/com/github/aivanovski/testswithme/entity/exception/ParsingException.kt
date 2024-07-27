package com.github.aivanovski.testswithme.entity.exception

class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)