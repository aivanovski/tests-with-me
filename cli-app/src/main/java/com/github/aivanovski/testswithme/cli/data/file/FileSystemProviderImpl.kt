package com.github.aivanovski.testswithme.cli.data.file

import arrow.core.Either
import arrow.core.raise.either
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.readText

class FileSystemProviderImpl : FileSystemProvider {

    override fun isDirectory(path: String): Boolean {
        return File(path).isDirectory
    }

    override fun getCurrentPath(): Either<IOException, String> =
        either {
            System.getProperty("user.dir")
        }

    override fun correctPath(path: String): Either<IOException, String> =
        either {
            if (exists(path)) {
                File(path).absolutePath
            } else {
                raise(FileNotFoundException(path))
            }
        }

    override fun exists(path: String): Boolean {
        return File(path).exists()
    }

    override fun read(path: String): Either<IOException, String> =
        either {
            val file = Path(path)

            try {
                file.readText()
            } catch (exception: IOException) {
                raise(exception)
            }
        }

    override fun getCurrentDirPath(): Either<IOException, String> =
        either {
            val currentDir = System.getProperty(PROPERTY_USER_DIR)

            if (currentDir.isNullOrBlank()) {
                raise(IOException("Unable to get system property: $PROPERTY_USER_DIR"))
            }

            currentDir
        }

    companion object {
        private const val PROPERTY_USER_DIR = "user.dir"
    }
}