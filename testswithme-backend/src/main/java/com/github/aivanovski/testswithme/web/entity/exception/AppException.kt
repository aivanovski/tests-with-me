package com.github.aivanovski.testswithme.web.entity.exception

open class AppException(
    message: String?,
    cause: Throwable?
) : Exception(message, cause) {

    constructor(message: String) : this(message, null)
    constructor(cause: Throwable) : this(null, cause)
}