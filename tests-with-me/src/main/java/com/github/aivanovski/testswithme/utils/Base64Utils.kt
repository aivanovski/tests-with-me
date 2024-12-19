package com.github.aivanovski.testswithme.utils

import arrow.core.Either
import arrow.core.raise.either
import java.util.Base64

object Base64Utils {

    fun decodeToBytes(base64: String): Either<Exception, ByteArray> =
        either {
            try {
                Base64.getDecoder().decode(base64)
            } catch (exception: IllegalArgumentException) {
                raise(exception)
            }
        }

    fun decode(base64: String): Either<Exception, String> =
        either {
            try {
                Base64.getDecoder().decode(base64).toString(Charsets.UTF_8)
            } catch (exception: IllegalArgumentException) {
                raise(exception)
            }
        }

    fun encode(text: String): String {
        return Base64.getEncoder().encodeToString(text.toByteArray())
    }

    fun encode(data: ByteArray): String {
        return Base64.getEncoder().encodeToString(data)
    }
}