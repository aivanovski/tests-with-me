package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.web.api.dto.UserItemDto
import com.github.aivanovski.testswithme.web.entity.User

fun User.toDto(): UserItemDto {
    return UserItemDto(
        id = uid.toString(),
        name = name
    )
}