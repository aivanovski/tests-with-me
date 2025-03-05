package com.github.aivanovski.testswithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.entity.AbsolutePath
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.extensions.toFile
import org.eclipse.jgit.api.Git

class GetLocalRepositoryLastCommitUseCase {

    fun getLastCommitHash(
        repositoryPath: AbsolutePath
    ): Either<AppException, String> =
        either {
            val git = Either.catch {
                Git.init()
                    .setDirectory(repositoryPath.toFile())
                    .call()
            }
                .mapLeft { error -> AppException(cause = error) }
                .bind()

            val head = git.repository.refDatabase.findRef("HEAD")?.target
                ?: raise(AppException("Failed to find HEAD reference"))

            val headCommitSha = head.objectId.name

            headCommitSha
        }
}