package com.github.aivanovski.testswithme.android.utils

import java.util.Base64
import timber.log.Timber

// TODO: remove
object Base64Utils {

    fun decode(base64: String): String? {
        return try {
            Base64.getDecoder().decode(base64)
                .toString(Charsets.UTF_8)
        } catch (exception: IllegalArgumentException) {
            Timber.e(exception)
            null
        }
    }

    fun encode(text: String): String {
        return Base64.getEncoder().encodeToString(text.toByteArray())
    }
}