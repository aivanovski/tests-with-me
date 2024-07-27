package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.UserDao
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.UserNotFoundByNameException
import com.github.aivanovski.testswithme.web.entity.exception.UserNotFoundByUidException

class UserRepository(
    private val dao: UserDao
) {

    fun getUsers(): Either<AppException, List<User>> {
        return Either.Right(dao.getAll())
    }

    fun getUserByName(name: String): Either<AppException, User> =
        either {
            val user = dao.findByName(name)
                ?: raise(UserNotFoundByNameException(name))

            user
        }

    fun add(user: User): Either<AppException, User> =
        either {
            dao.add(user)

            dao.findByUid(user.uid)
                ?: raise(UserNotFoundByUidException(user.uid))
        }
}