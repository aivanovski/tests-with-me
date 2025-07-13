package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.entity.HashType
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.web.api.dto.FlowItemDto
import com.github.aivanovski.testswithme.web.api.dto.Sha256HashDto
import com.github.aivanovski.testswithme.web.api.dto.UserItemDto
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.User

fun User.toDto(): UserItemDto =
    UserItemDto(
        id = uid.toString(),
        name = name
    )

fun Hash.toDto(): Sha256HashDto =
    when (type) {
        HashType.SHA_256 -> Sha256HashDto(value)
    }

fun Flow.toDto(content: String): FlowItemDto =
    FlowItemDto(
        id = uid.toString(),
        projectId = projectUid.toString(),
        groupId = groupUid.toString(),
        name = name,
        base64Content = Base64Utils.encode(content),
        contentHash = contentHash.toDto()
    )