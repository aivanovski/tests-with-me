package com.github.aivanovski.testwithme.android.utils

import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.entity.ErrorMessage

fun Exception.formatErrorMessage(
    resourceProvider: ResourceProvider
): ErrorMessage {
    val exception = this

    exception.printStackTrace()

    return ErrorMessage(
        message = "Error has been occurred", // TODO: string resource
        cause = exception
    )
}
