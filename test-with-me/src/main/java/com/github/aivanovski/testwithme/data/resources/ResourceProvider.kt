package com.github.aivanovski.testwithme.data.resources

import arrow.core.Either
import java.io.IOException

interface ResourceProvider {
    fun read(filename: String): Either<IOException, String>
}