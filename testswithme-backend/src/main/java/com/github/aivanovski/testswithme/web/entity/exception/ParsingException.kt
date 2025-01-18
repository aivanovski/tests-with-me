package com.github.aivanovski.testswithme.web.entity.exception

open class ParsingException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class InvalidBase64String : ParsingException(
    message = "Invalid Base64 string"
)

class InvalidUidString(
    uid: String
) : ParsingException(
    message = "Invalid uid string: $uid"
)