package com.github.aivanovski.testswithme.web.data.file

import arrow.core.Either
import arrow.core.raise.either
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.readBytes

class FileSystemProviderImpl : FileSystemProvider {

    override fun getCurrentDirPath(): Either<IOException, String> =
        either {
            val currentDir = System.getProperty(PROPERTY_USER_DIR)

            if (currentDir.isNullOrBlank()) {
                raise(IOException("Unable to get system property: $PROPERTY_USER_DIR"))
            }

            currentDir
        }

    override fun readBytes(relativePath: String): Either<IOException, ByteArray> =
        either {
            val currentDir = getCurrentDirPath().bind()

            // Add check that absolutePath is still inside currentDir
            val absolutePath = if (relativePath.startsWith("/")) {
                currentDir + relativePath
            } else {
                "$currentDir/$relativePath"
            }

            val file = Path(absolutePath)

            try {
                file.readBytes()
            } catch (exception: IOException) {
                raise(exception)
            }
        }

    companion object {
        private const val PROPERTY_USER_DIR = "user.dir"
    }
}