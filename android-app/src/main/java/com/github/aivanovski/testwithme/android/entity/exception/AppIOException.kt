package com.github.aivanovski.testwithme.android.entity.exception

import java.io.File

open class AppIOException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

class FileNotFoundException(
    file: File
) : AppIOException(
    "Failed to find file: ${file.path}"
)