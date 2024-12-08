package com.github.aivanovski.testswithme.cli.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.entity.exception.AppException
import java.nio.file.Path

class ReferenceResolver {

    fun resolveFlowByNameOrPath(nameOrPath: String): Either<AppException, Path> =
        either {
            // TODO: implement
            Path.of("")
        }
}