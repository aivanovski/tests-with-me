package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.TestSourceDao
import com.github.aivanovski.testswithme.web.entity.TestSource
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidEntityIdException

class TestSourceRepository(
    private val dao: TestSourceDao
) {

    fun getAll(): Either<AppException, List<TestSource>> =
        either {
            dao.getAll()
        }

    fun getByUid(uid: Uid): Either<AppException, TestSource> =
        either {
            dao.findByUid(uid)
                ?: raise(EntityNotFoundByUidException(TestSource::class, uid))
        }

    fun add(source: TestSource): Either<AppException, TestSource> =
        either {
            dao.add(source)

            getByUid(source.uid).bind()
        }

    fun update(source: TestSource): Either<AppException, TestSource> =
        either {
            if (source.id == 0L) {
                raise(InvalidEntityIdException(TestSource::class))
            }

            dao.update(source)
        }
}