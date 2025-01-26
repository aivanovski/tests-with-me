package com.github.aivanovski.testswithme.android.entity.exception

import kotlin.reflect.KClass

open class DaoException(
    message: String? = null,
    cause: Exception? = null
) : AppException(message, cause)

open class FailedToFindEntityException(
    entityName: String,
    fieldName: String,
    fieldValue: String
) : DaoException(
    message = "Failed to find entity %s: %s=%s".format(entityName, fieldName, fieldValue),
    cause = null
)

class FailedToFindEntityByNameException(
    entityType: KClass<*>,
    name: String
) : FailedToFindEntityException(
    entityName = entityType.java.simpleName,
    fieldName = "name",
    fieldValue = name
)

class FailedToFindEntityByUidException(
    entityType: KClass<*>,
    uid: String
) : FailedToFindEntityException(
    entityName = entityType.java.simpleName,
    fieldName = "uid",
    fieldValue = uid
)

class UserNotFoundException : DaoException(message = "Failed find current user")