package com.github.aivanovski.testswithme.cli.entity.exception

class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)