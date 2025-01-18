package com.github.aivanovski.testswithme.web.entity.exception

import com.github.aivanovski.testswithme.web.presentation.Errors.INVALID_PARAMETER

class InvalidParameterException(
    parameterName: String
) : AppException(
    message = INVALID_PARAMETER.format(parameterName)
)