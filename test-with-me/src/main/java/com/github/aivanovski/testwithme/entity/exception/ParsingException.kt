package com.github.aivanovski.testwithme.entity.exception

class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)