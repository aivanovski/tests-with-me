package com.github.aivanovski.testswithme.cli.entity.exception

open class AdbException(
    message: String? = null,
    cause: Exception? = null
) : AppException()

class FailedToFindDeviceException : AdbException("Failed to find device")