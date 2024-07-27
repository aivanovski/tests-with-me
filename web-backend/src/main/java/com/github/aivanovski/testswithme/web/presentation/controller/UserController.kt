package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.api.UsersItemDto
import com.github.aivanovski.testswithme.web.api.response.UsersResponse
import com.github.aivanovski.testswithme.web.data.repository.UserRepository
import com.github.aivanovski.testswithme.web.entity.ErrorResponse
import com.github.aivanovski.testswithme.web.extensions.transformError

class UserController(
    private val userRepository: UserRepository
) {

    fun getUsers(): Either<ErrorResponse, UsersResponse> =
        either {
            val users = userRepository.getUsers()
                .transformError()
                .bind()

            UsersResponse(
                users = users.map { user ->
                    UsersItemDto(
                        id = user.uid.toString(),
                        name = user.name
                    )
                }
            )
        }
}