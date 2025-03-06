package com.github.aivanovski.testswithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.network.HttpRequestExecutor
import com.github.aivanovski.testswithme.web.data.network.model.CommitDto
import com.github.aivanovski.testswithme.web.entity.GithubRepository
import com.github.aivanovski.testswithme.web.entity.exception.AppException

class GetRemoteRepositoryLastCommitUseCase(
    private val executor: HttpRequestExecutor
) {

    suspend fun getLastCommitHash(repo: GithubRepository): Either<AppException, String?> =
        either {
            val url =
                "https://api.github.com/repos/${repo.userName}/${repo.repositoryName}/commits"

            val response = executor.get<Array<CommitDto>>(url).bind()

            response.firstOrNull()?.sha
        }
}