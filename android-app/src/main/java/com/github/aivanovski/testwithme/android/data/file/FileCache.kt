package com.github.aivanovski.testwithme.android.data.file

import arrow.core.Either
import com.github.aivanovski.testwithme.android.entity.exception.AppException

interface FileCache {
    fun put(uid: String, content: String): Either<AppException, Unit>
    fun get(uid: String): Either<AppException, String>
    fun getOrNull(uid: String): Either<AppException, String?>
}