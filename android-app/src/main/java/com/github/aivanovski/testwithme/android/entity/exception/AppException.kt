package com.github.aivanovski.testwithme.android.entity.exception

open class AppException(
    message: String? = null,
    cause: Exception? = null
) : Exception(message, cause)