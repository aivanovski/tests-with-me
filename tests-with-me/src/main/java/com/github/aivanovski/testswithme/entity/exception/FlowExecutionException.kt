package com.github.aivanovski.testswithme.entity.exception

import com.github.aivanovski.testswithme.flow.error.FlowError

open class FlowExecutionException(
    val error: FlowError? = null,
    message: String? = null,
    cause: Exception? = null
) : TestsWithMeException(message, cause) {

    companion object {
        fun fromFlowError(error: FlowError): FlowExecutionException {
            return FlowExecutionException(
                error = error,
                message = error.cause
            )
        }
    }
}

open class ExternalException(
    cause: Exception
) : FlowExecutionException(cause = cause)

class StepVerificationException(
    cause: Exception
) : ExternalException(cause = cause)