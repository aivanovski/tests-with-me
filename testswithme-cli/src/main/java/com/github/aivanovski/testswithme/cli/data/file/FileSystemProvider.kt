package com.github.aivanovski.testswithme.cli.data.file

import arrow.core.Either
import java.io.IOException

interface FileSystemProvider {
    fun isDirectory(path: String): Boolean
    fun getCurrentPath(): Either<IOException, String>
    fun correctPath(path: String): Either<IOException, String>
    fun exists(path: String): Boolean
    fun read(path: String): Either<IOException, String>
    fun getCurrentDirPath(): Either<IOException, String>
}