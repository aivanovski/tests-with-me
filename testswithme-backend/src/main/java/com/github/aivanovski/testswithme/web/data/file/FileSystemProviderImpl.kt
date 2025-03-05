package com.github.aivanovski.testswithme.web.data.file

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.entity.AbsolutePath
import com.github.aivanovski.testswithme.web.entity.RelativePath
import com.github.aivanovski.testswithme.web.entity.exception.AppIoException
import com.github.aivanovski.testswithme.web.extensions.isDirectory
import com.github.aivanovski.testswithme.web.extensions.toPath
import com.github.aivanovski.testswithme.web.extensions.toRelative
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.LinkedList
import java.util.Queue
import java.util.stream.Collectors
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.notExists
import kotlin.io.path.readBytes

class FileSystemProviderImpl(
    private val baseDirPath: String? = null
) : FileSystemProvider {

    override fun getBaseDirPath(): Either<AppIoException, AbsolutePath> =
        either {
            val basePath = if (baseDirPath == null) {
                val dirPath = System.getProperty(PROPERTY_USER_DIR)

                if (dirPath.isNullOrBlank()) {
                    raise(AppIoException("Unable to get system property: $PROPERTY_USER_DIR"))
                }

                dirPath
            } else {
                baseDirPath
            }

            val dir = Paths.get(basePath)
            if (dir.notExists()) {
                Either.catch { dir.createDirectories() }
                    .mapLeft { error -> AppIoException(cause = error) }
                    .bind()
            }

            AbsolutePath(
                basePath = dir.toString(),
                relativePath = ""
            )
        }

    override fun getDirPath(path: RelativePath): Either<AppIoException, AbsolutePath> =
        either {
            val absolutePath = convertToAbsolute(path).bind()

            createDirectories(absolutePath).bind()
        }

    override fun readBytes(path: RelativePath): Either<AppIoException, ByteArray> =
        either {
            val file = convertToAbsolute(path)
                .bind()
                .toPath()

            try {
                file.readBytes()
            } catch (exception: IOException) {
                raise(AppIoException(cause = exception))
            }
        }

    override fun getParent(path: RelativePath): Either<AppIoException, AbsolutePath> =
        either {
            val absolutePath = convertToAbsolute(path).bind()

            val parent = absolutePath.toPath().parent

            AbsolutePath(
                basePath = absolutePath.basePath,
                relativePath = parent.toString().removePrefix(absolutePath.basePath)
            )
        }

    override fun listFiles(path: RelativePath): Either<AppIoException, List<AbsolutePath>> =
        either {
            val absolutePath = convertToAbsolute(path).bind()

            val files = Either.catch {
                Files.list(absolutePath.toPath()).use {
                    it.collect(Collectors.toList())
                }
            }
                .mapLeft { error -> AppIoException(cause = error) }
                .bind()

            files.map { file ->
                AbsolutePath(
                    basePath = absolutePath.basePath,
                    relativePath = file.absolutePathString().removePrefix(absolutePath.basePath)
                )
            }
        }

    override fun listFileTree(
        path: RelativePath,
        maxDepth: Int
    ): Either<AppIoException, List<List<AbsolutePath>>> =
        either {
            val rootAbsPath = convertToAbsolute(path).bind()
            if (!rootAbsPath.isDirectory()) {
                return@either emptyList()
            }

            val result = mutableListOf<MutableList<AbsolutePath>>()
            val queue: Queue<AbsolutePath> = LinkedList<AbsolutePath>()
                .apply {
                    add(rootAbsPath)
                }

            var depth = 0
            while (queue.isNotEmpty() && depth < maxDepth) {
                val count = queue.size
                val layer = mutableListOf<AbsolutePath>()

                repeat(count) {
                    val path = queue.poll()

                    for (childFile in listFiles(path.toRelative()).bind()) {
                        layer.add(childFile)

                        if (childFile.isDirectory()) {
                            queue.add(childFile)
                        }
                    }
                }

                result.add(layer)

                depth++
            }

            result
        }

    @OptIn(ExperimentalPathApi::class)
    override fun remove(path: RelativePath): Either<AppIoException, Unit> =
        either {
            val absolutePath = convertToAbsolute(path).bind()
            absolutePath.toPath().deleteRecursively()
        }

    private fun createDirectories(path: AbsolutePath): Either<AppIoException, AbsolutePath> =
        either {
            val dir = path.toPath()
            if (dir.notExists()) {
                Either.catch { dir.createDirectories() }
                    .mapLeft { error -> AppIoException(cause = error) }
                    .bind()
            }

            path
        }

    private fun convertToAbsolute(path: RelativePath): Either<AppIoException, AbsolutePath> =
        either {
            val baseDir = getBaseDirPath().bind()

            // TODO: Add check that absolutePath is still inside currentDir
            AbsolutePath(
                basePath = baseDir.path,
                relativePath = path.relativePath.removePrefix("/")
            )
        }

    companion object {
        private const val PROPERTY_USER_DIR = "user.dir"
        const val DATA_DIRECTORY = "app-data"
        const val GIT_DIRECTORY = "git"
    }
}