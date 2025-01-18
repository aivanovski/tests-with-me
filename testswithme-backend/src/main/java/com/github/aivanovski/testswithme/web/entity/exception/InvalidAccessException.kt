package com.github.aivanovski.testswithme.web.entity.exception

import kotlin.reflect.KClass

open class InvalidAccessException(
    message: String
) : AppException(
    message = message
)

class DeletedEntityAccessException(
    type: KClass<*>
) : InvalidAccessException(
    message = "Access to deleted entity: ${type.simpleName}"
)