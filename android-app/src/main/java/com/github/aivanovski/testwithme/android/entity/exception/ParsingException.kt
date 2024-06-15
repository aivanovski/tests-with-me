package com.github.aivanovski.testwithme.android.entity.exception

class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)