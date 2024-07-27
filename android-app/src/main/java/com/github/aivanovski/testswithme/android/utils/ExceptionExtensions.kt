package com.github.aivanovski.testswithme.android.utils

import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.ErrorMessage

fun Exception.formatErrorMessage(resourceProvider: ResourceProvider): ErrorMessage {
    val exception = this

    exception.printStackTrace()

    return ErrorMessage(
        message = "Error has been occurred", // TODO: string resource
        cause = exception
    )
}