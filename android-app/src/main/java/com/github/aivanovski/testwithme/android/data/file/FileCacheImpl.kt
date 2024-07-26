package com.github.aivanovski.testwithme.android.data.file

import android.content.Context
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.AppIOException
import com.github.aivanovski.testwithme.android.entity.exception.FileNotFoundException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FileCacheImpl(
    private val context: Context
) : FileCache {

    override fun put(
        uid: String,
        content: String
    ): Either<AppException, Unit> = either {
        val cacheDir = getCacheDir().bind()

        try {
            val file = File(cacheDir, uid)
            val out = FileOutputStream(file)
            out.bufferedWriter().use { writer ->
                writer.write(content)
                writer.flush()
            }
        } catch (exception: IOException) {
            raise(AppIOException(cause = exception))
        }
    }

    override fun get(
        uid: String
    ): Either<AppException, String> = either {
        val cacheDir = getCacheDir().bind()

        val file = File(cacheDir, uid)
        if (!file.exists()) {
            raise(FileNotFoundException(file))
        }

        try {
            file.readText()
        } catch (exception: IOException) {
            raise(AppIOException(cause = exception))
        }
    }

    override fun getOrNull(
        uid: String
    ): Either<AppException, String?> = either {
        val cacheDir = getCacheDir().bind()

        val file = File(cacheDir, uid)
        if (!file.exists()) {
            return@either null
        }

        try {
            file.readText()
        } catch (exception: IOException) {
            raise(AppIOException(cause = exception))
        }
    }

    private fun getCacheDir(): Either<AppException, File> = either {
        val dir = context.cacheDir
        if (dir.exists()) {
            dir
        } else {
            raise(FileNotFoundException(dir))
        }
    }
}