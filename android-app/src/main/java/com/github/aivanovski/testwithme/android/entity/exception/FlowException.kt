package com.github.aivanovski.testwithme.android.entity.exception

import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException

class FlowException(
    cause: FlowExecutionException
) : AppException(cause = cause)