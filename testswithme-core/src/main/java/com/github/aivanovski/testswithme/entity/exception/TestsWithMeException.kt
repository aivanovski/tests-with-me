package com.github.aivanovski.testswithme.entity.exception

open class TestsWithMeException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)