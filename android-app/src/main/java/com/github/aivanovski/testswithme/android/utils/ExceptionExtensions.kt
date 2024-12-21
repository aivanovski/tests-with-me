package com.github.aivanovski.testswithme.android.utils

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.extensions.getRootCause

fun Exception.formatErrorMessage(resourceProvider: ResourceProvider): ErrorMessage {
    val exception = this

    exception.printStackTrace()

    val cause = this.getRootCause()

    val message = when {
        !cause.message.isNullOrBlank() -> cause.message.orEmpty()
        else -> resourceProvider.getString(R.string.error_has_been_occurred)
    }

    return ErrorMessage(
        message = message,
        cause = exception
    )
}