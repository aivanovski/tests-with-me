package com.github.aivanovski.testswithme.android.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCurrentJobUseCase(
    private val repository: JobRepository
) {

    suspend fun getCurrentJob(): Either<AppException, JobEntry?> =
        withContext(Dispatchers.IO) {
            either {
                val entry = repository.getAll().firstOrNull { entry ->
                    entry.status == JobStatus.RUNNING
                }

                entry
            }
        }
}