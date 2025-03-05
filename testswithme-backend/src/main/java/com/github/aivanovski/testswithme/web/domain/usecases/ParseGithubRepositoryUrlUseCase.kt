package com.github.aivanovski.testswithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.entity.GithubRepository
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException

class ParseGithubRepositoryUrlUseCase {

    private val githubUrlRegex = Regex(
        "^https?://github\\.com/(?<user>[a-zA-Z0-9-]+)/(?<repo>[a-zA-Z0-9._-]+)(\\.git)?/?$"
    )

    fun parseRepositoryUrl(url: String): Either<ParsingException, GithubRepository> =
        either {
            val matchResult = githubUrlRegex.matchEntire(url)
                ?: raise(ParsingException(message = "Invalid url format"))

            GithubRepository(
                userName = matchResult.groups["user"]?.value.orEmpty(),
                repositoryName = matchResult.groups["repo"]?.value.orEmpty().removeSuffix(".git")
            )
        }
}