package com.github.aivanovski.testswithme.android.entity.exception

open class UserInputValidationException(
    message: String,
    cause: Exception? = null
) : AppException(message = message, cause = cause)