package com.github.aivanovski.testswithme.web.entity.exception

import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.presentation.Errors.ENTITY_NOT_FOUND
import kotlin.reflect.KClass

open class FailedToFindEntityException(
    entity: String,
    key: String,
    value: String
) : AppException(ENTITY_NOT_FOUND.format(entity, key, value))

class FailedToFindEntityByUidException : FailedToFindEntityException {

    constructor(
        type: KClass<*>,
        uid: Uid
    ) : super(
        entity = type.java.simpleName,
        key = "uid",
        value = uid.toString()
    )

    constructor(
        type: KClass<*>,
        uid: String
    ) : super(
        entity = type.java.simpleName,
        key = "uid",
        value = uid
    )
}

class FailedToFindEntityByNameException(
    type: KClass<*>,
    name: String
) : FailedToFindEntityException(
    entity = type.java.simpleName,
    key = "name",
    value = name
)