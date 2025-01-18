package com.github.aivanovski.testswithme.web.data.file

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.entity.FsPath
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.AppIoException
import com.github.aivanovski.testswithme.web.entity.exception.FileNotFoundException
import com.github.aivanovski.testswithme.web.extensions.toPath
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.writeText

class FileStorage {

    fun putContent(
        destination: StorageDestination,
        path: FsPath,
        content: String
    ): Either<AppException, Unit> =
        either {
            val filesDir = getDirectory(destination).bind()

            val file = path.toPath(base = filesDir.pathString)

            val parent = file.parent
            if (!parent.exists()) {
                try {
                    parent.createDirectories()
                } catch (exception: IOException) {
                    raise(AppException(cause = exception))
                }
            }

            try {
                file.writeText(content)
            } catch (exception: IOException) {
                raise(AppIoException(cause = exception))
            }
        }

    fun getContent(
        destination: StorageDestination,
        path: FsPath
    ): Either<AppException, String> =
        either {
            val dir = getDirectory(destination).bind()

            val file = path.toPath(base = dir.pathString)
            if (!file.exists()) {
                raise(FileNotFoundException(path.path))
            }

            try {
                file.readText()
            } catch (exception: IOException) {
                raise(AppIoException(cause = exception))
            }
        }

    private fun getDirectory(dir: StorageDestination): Either<AppException, Path> =
        either {
            val currentDir = System.getProperty("user.dir")
            if (currentDir.isNullOrBlank()) {
                raise(AppException("Unable to get current dir"))
            }

            val path = Path(currentDir, ALL_FILES_DIRECTORY + "/" + dir.path)
            if (!path.exists()) {
                try {
                    path.createDirectories()
                } catch (exception: IOException) {
                    raise(AppException(cause = exception))
                }
            }

            path
        }

    enum class StorageDestination(val path: String) {
        FLOWS(path = "flows"),
        REPORTS(path = "reports")
    }

    companion object {
        private const val ALL_FILES_DIRECTORY = "app-data"
    }
}