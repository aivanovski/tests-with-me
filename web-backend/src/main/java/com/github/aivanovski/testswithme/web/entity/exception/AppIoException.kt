package com.github.aivanovski.testswithme.web.entity.exception

open class AppIoException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class FileNotFoundException(
    path: String
) : AppIoException(
    message = "Failed to find file: %s".format(path)
)