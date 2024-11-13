package com.github.aivanovski.testswithme.data.json

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.exception.ParsingException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonSerializer {

    val json = createJson()

    private fun createJson(): Json {
        return Json {
            explicitNulls = false
            ignoreUnknownKeys = true
        }
    }

    inline fun <reified T> deserialize(text: String): Either<ParsingException, T> =
        either {
            try {
                json.decodeFromString(text)
            } catch (exception: SerializationException) {
                raise(ParsingException(cause = exception))
            }
        }

    inline fun <reified T> serialize(data: T): String = json.encodeToString(data)
}