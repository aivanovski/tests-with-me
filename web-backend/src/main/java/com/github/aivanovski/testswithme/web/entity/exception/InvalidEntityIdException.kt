package com.github.aivanovski.testswithme.web.entity.exception

import kotlin.reflect.KClass

class InvalidEntityIdException(
    type: KClass<*>
) : AppException(
    message = "Invalid entity id: ${type.simpleName}"
)