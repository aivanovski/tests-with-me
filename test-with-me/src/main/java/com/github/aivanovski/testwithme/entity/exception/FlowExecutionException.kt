package com.github.aivanovski.testwithme.entity.exception

open class FlowExecutionException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)