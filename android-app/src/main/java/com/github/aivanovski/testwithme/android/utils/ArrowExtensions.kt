package com.github.aivanovski.testwithme.android.utils

import arrow.core.Either
import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.entity.ErrorMessage
import com.github.aivanovski.testwithme.extensions.unwrapError

fun Either<Exception, Any>.formatError(resourceProvider: ResourceProvider): ErrorMessage {
    return unwrapError().formatErrorMessage(resourceProvider)
}