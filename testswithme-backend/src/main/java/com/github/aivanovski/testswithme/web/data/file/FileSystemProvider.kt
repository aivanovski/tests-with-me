package com.github.aivanovski.testswithme.web.data.file

import arrow.core.Either
import java.io.IOException

interface FileSystemProvider {
    fun getCurrentDirPath(): Either<IOException, String>
    fun readBytes(relativePath: String): Either<IOException, ByteArray>
}