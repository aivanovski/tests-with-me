package com.github.aivanovski.testwithme.web.entity

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.web.entity.exception.AppException
import com.github.aivanovski.testwithme.web.entity.exception.ParsingException
import java.util.UUID

data class Uid(
    val uid: String
) {

    override fun toString(): String {
        return uid
    }

    fun append(another: Uid): Uid {
        return Uid(
            uid = "$uid:${another.uid}"
        )
    }

    companion object {

        fun generate(): Uid {
            return Uid(
                uid = UUID.randomUUID().toString()
            )
        }

        fun parse(value: String): Either<AppException, Uid> = either {
            if (value.isNotBlank() && value.length > 16) {
                Uid(value)
            } else {
                raise(ParsingException("Failed to parse uid: $value"))
            }
        }

        fun createFrom(first: Int, second: Int): Uid {
            return Uid(
                uid = UUID(first.toLong(), second.toLong()).toString()
            )
        }
    }
}