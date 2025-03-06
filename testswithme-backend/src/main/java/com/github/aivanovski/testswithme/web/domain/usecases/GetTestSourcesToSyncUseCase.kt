package com.github.aivanovski.testswithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.web.data.repository.TestSourceRepository
import com.github.aivanovski.testswithme.web.entity.TestSource
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.extensions.isNecessaryToSync
import kotlin.time.Duration.Companion.hours

class GetTestSourcesToSyncUseCase(
    private val projectRepository: ProjectRepository,
    private val testSourceRepository: TestSourceRepository
) {

    fun getTestSourcesToSync(): Either<AppException, List<TestSource>> =
        either {
            val sourceUids = projectRepository.getAll()
                .bind()
                .mapNotNull { project -> project.testSourceUid }

            testSourceRepository.getAll()
                .bind()
                .filter { source ->
                    source.uid in sourceUids && source.isNecessaryToSync()
                }
        }

    companion object {
        val SYNC_INTERVAL = 12.hours.inWholeMilliseconds
    }
}