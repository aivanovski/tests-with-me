package com.github.aivanovski.testswithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.web.data.file.FileSystemProviderImpl.Companion.DATA_DIRECTORY
import com.github.aivanovski.testswithme.web.data.file.FileSystemProviderImpl.Companion.GIT_DIRECTORY
import com.github.aivanovski.testswithme.web.entity.AbsolutePath
import com.github.aivanovski.testswithme.web.entity.RelativePath
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.extensions.toFile
import org.eclipse.jgit.api.Git
import org.slf4j.LoggerFactory

class CloneGitRepositoryUseCase(
    private val fileSystemProvider: FileSystemProvider
) {

    fun cloneGitRepository(
        repositoryUrl: String,
    ): Either<AppException, AbsolutePath> =
        either {
            val dirName = Uid.generate().toString()
            val destination = fileSystemProvider.getDirPath(
                path = RelativePath("$DATA_DIRECTORY/$GIT_DIRECTORY/$dirName")
            ).bind()

            LOGGER.debug("Cloning {} into {}", repositoryUrl, destination.path)

            val startTime = System.currentTimeMillis()

            Either.catch {
                Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(destination.toFile())
                    .call()
            }
                .mapLeft { error -> AppException(cause = error) }
                .bind()

            val timeTook = System.currentTimeMillis() - startTime
            LOGGER.debug("Repository {} cloned successfully, took {} ms", repositoryUrl, timeTook)

            destination
        }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CloneGitRepositoryUseCase::class.java)
    }
}