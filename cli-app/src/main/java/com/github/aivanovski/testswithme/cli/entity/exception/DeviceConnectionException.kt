package com.github.aivanovski.testswithme.cli.entity.exception

open class DeviceConnectionException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class FailedToFindDeviceException : DeviceConnectionException("Failed to find device")