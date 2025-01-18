package com.github.aivanovski.testswithme.data.resources

import arrow.core.Either
import java.io.IOException

interface ResourceProvider {
    fun read(filename: String): Either<IOException, String>
    fun readBytes(filename: String): Either<IOException, ByteArray>
}