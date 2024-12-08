package com.github.aivanovski.testswithme.entity.exception

open class ReferenceException(
    message: String? = null,
    cause: Exception? = null
) : TestsWithMeException(message, cause)

class InvalidReferencePathException(
    val path: String
) : ReferenceException(
    message = "Invalid reference path: %s".format(path)
)

class InvalidReferenceNameException(
    val name: String
) : ReferenceException(
    message = "Invalid name: %s".format(name)
)