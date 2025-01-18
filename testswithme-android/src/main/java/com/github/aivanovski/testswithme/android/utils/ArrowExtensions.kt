package com.github.aivanovski.testswithme.android.utils

import arrow.core.Either
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.extensions.unwrapError

fun Either<Exception, Any>.formatError(resourceProvider: ResourceProvider): ErrorMessage {
    return unwrapError().formatErrorMessage(resourceProvider)
}