package com.github.aivanovski.testwithme.web.entity.exception

import com.github.aivanovski.testwithme.web.presentation.Errors.INVALID_PARAMETER

class InvalidParameterException(
    parameterName: String
) : AppException(
    message = INVALID_PARAMETER.format(parameterName)
)