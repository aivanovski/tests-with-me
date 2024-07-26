package com.github.aivanovski.testwithme.entity.exception

open class FlowExecutionException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)

open class ExternalException(
    cause: Exception
) : FlowExecutionException(cause = cause)

class StepVerificationException(
    cause: Exception
) : ExternalException(cause = cause)