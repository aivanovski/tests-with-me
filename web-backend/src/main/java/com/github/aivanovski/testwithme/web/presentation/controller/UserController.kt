package com.github.aivanovski.testwithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.web.api.UsersItemDto
import com.github.aivanovski.testwithme.web.api.response.UsersResponse
import com.github.aivanovski.testwithme.web.data.repository.UserRepository
import com.github.aivanovski.testwithme.web.entity.ErrorResponse
import com.github.aivanovski.testwithme.web.extensions.transformError

class UserController(
    private val userRepository: UserRepository
) {

    fun getUsers(): Either<ErrorResponse, UsersResponse> = either {
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