package com.github.aivanovski.testwithme.android.entity.exception

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
    message = "Unable to find entity %s: %s=%s".format(entityName, fieldName, fieldValue),
    cause = null
)

class FailedToFindEntityByUidException(
    entityType: KClass<*>,
    uid: String
) : FailedToFindEntityException(
    entityName = entityType.java.simpleName,
    fieldName = "uid",
    fieldValue = uid
)