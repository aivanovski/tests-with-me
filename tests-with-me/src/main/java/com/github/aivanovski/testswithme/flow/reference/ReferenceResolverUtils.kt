package com.github.aivanovski.testswithme.flow.reference

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.exception.InvalidReferenceNameException
import com.github.aivanovski.testswithme.entity.exception.ReferenceException

object ReferenceResolverUtils {

    fun resolveGroupsAndName(pathOrName: String): Either<ReferenceException, GroupsAndFlow> =
        either {
            val values = pathOrName
                .split("/")
                .mapNotNull { value ->
                    if (value.isNotBlank()) {
                        value.trim()
                    } else {
                        null
                    }
                }

            when {
                values.size > 1 -> {
                    GroupsAndFlow(
                        groupNames = values.subList(0, values.size - 1),
                        flowName = values.last()
                    )
                }

                values.size == 1 -> {
                    GroupsAndFlow(
                        groupNames = emptyList(),
                        flowName = values.first()
                    )
                }

                else -> {
                    raise(InvalidReferenceNameException(name = pathOrName))
                }
            }
        }

    data class GroupsAndFlow(
        val groupNames: List<String>,
        val flowName: String
    )
}