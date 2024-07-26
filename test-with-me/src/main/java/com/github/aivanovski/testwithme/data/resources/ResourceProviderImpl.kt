package com.github.aivanovski.testwithme.data.resources

import arrow.core.Either
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import kotlin.reflect.KClass

class ResourceProviderImpl(
    private val packageType: KClass<*>
) : ResourceProvider {

    override fun read(filename: String): Either<IOException, String> {
        return try {
            val content = packageType.java.classLoader
                .getResourceAsStream(filename)

            if (content != null) {
                Either.Right(InputStreamReader(content).readText())
            } else {
                Either.Left(FileNotFoundException(filename))
            }
        } catch (exception: IOException) {
            Either.Left(exception)
        }
    }
}