package com.github.aivanovski.testwithme.web.entity.exception

open class BadRequestException(
    message: String
) : AppException(message)

class InvalidRequestFieldException(
    fieldName: String
) : BadRequestException(
    message = "Invalid request field: $fieldName"
)

class EmptyRequestFieldException(
    fieldName: String
) : BadRequestException(
    message = "Field can't be empty: $fieldName"
)

class EntityAlreadyExistsException(
    key: String
) : BadRequestException(
    message = "Entity already exists: $key"
)