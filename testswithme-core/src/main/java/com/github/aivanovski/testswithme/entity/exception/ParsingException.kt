package com.github.aivanovski.testswithme.entity.exception

open class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : TestsWithMeException(message, cause)