package com.github.aivanovski.testswithme.web.entity.exception

import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.presentation.Errors.ENTITY_NOT_FOUND
import kotlin.reflect.KClass

open class EntityNotFoundException(
    entity: String,
    key: String,
    value: String
) : AppException(ENTITY_NOT_FOUND.format(entity, key, value))

// TODO: rename to FailedToFindEntity..
class EntityNotFoundByUidException : EntityNotFoundException {

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

class EntityNotFoundByNameException(
    type: KClass<*>,
    name: String
) : EntityNotFoundException(
    entity = type.java.simpleName,
    key = "name",
    value = name
)

// TODO: remove
class UserNotFoundByNameException(
    name: String
) : EntityNotFoundException(
    entity = User::class.java.simpleName,
    key = "name",
    value = name
)

// TODO: remove
class UserNotFoundByUidException(
    uid: Uid
) : EntityNotFoundException(
    entity = User::class.java.simpleName,
    key = "uid",
    value = uid.toString()
)

// TODO: remove
class FlowNotFoundByUidException(
    uid: String
) : EntityNotFoundException(
    entity = Flow::class.java.simpleName,
    key = "uid",
    value = uid
)

// TODO: remove
class ProjectNotFoundByUidException(
    uid: Uid
) : EntityNotFoundException(
    entity = Project::class.java.simpleName,
    key = "uid",
    value = uid.toString()
)