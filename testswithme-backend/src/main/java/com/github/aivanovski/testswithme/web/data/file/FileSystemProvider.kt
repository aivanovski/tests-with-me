package com.github.aivanovski.testswithme.web.data.file

import arrow.core.Either
import com.github.aivanovski.testswithme.web.entity.AbsolutePath
import com.github.aivanovski.testswithme.web.entity.RelativePath
import com.github.aivanovski.testswithme.web.entity.exception.AppIoException

interface FileSystemProvider {

    fun getBaseDirPath(): Either<AppIoException, AbsolutePath>

    fun getDirPath(path: RelativePath): Either<AppIoException, AbsolutePath>

    fun readBytes(path: RelativePath): Either<AppIoException, ByteArray>

    fun getParent(path: RelativePath): Either<AppIoException, AbsolutePath>

    fun listFiles(path: RelativePath): Either<AppIoException, List<AbsolutePath>>

    fun listFileTree(
        path: RelativePath,
        maxDepth: Int
    ): Either<AppIoException, List<List<AbsolutePath>>>

    fun remove(path: RelativePath): Either<AppIoException, Unit>
}