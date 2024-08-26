package com.github.aivanovski.testswithme.data.resources

import arrow.core.Either
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.reflect.KClass

class ResourceProviderImpl(
    private val packageType: KClass<*>
) : ResourceProvider {

    override fun read(filename: String): Either<IOException, String> {
        return readBytes(filename)
            .map { bytes -> String(bytes) }
    }

    override fun readBytes(filename: String): Either<IOException, ByteArray> {
        return try {
            val content = packageType.java.classLoader
                .getResourceAsStream(filename)

            if (content != null) {
                Either.Right(content.readBytes())
            } else {
                Either.Left(FileNotFoundException(filename))
            }
        } catch (exception: IOException) {
            Either.Left(exception)
        }
    }
}