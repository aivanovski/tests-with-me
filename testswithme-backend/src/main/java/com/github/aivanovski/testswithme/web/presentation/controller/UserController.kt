package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.api.response.UsersResponse
import com.github.aivanovski.testswithme.web.data.repository.UserRepository
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.extensions.toDto

class UserController(
    private val userRepository: UserRepository
) {

    fun getUsers(): Either<AppException, UsersResponse> =
        either {
            val users = userRepository.getUsers()
                .bind()

            UsersResponse(
                users = users.map { user -> user.toDto() }
            )
        }
}