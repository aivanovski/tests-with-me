package com.github.aivanovski.testwithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.db.dao.JobDao
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FailedToFindEntityException

class JobRepository(
    private val dao: JobDao
) {

    fun add(entry: JobEntry) {
        dao.insert(entry)
    }

    fun getAll(): List<JobEntry> {
        return dao.getAll()
            .sortedByDescending { entry -> entry.addedTimestamp }
    }

    fun getJobByUid(uid: String): Either<AppException, JobEntry> = either {
        val entry = dao.getByUid(uid) ?: raise(newFailedToFindEntityError(uid))

        entry
    }

    fun update(job: JobEntry): Either<AppException, Unit> = either {
        val existingJob = getJobByUid(job.uid).bind()

        dao.update(
            job.copy(
                id = existingJob.id
            )
        )
    }

    fun removeByUid(uid: String) {
        dao.removeByUid(uid)
    }

    private fun newFailedToFindEntityError(
        uid: String
    ): FailedToFindEntityException {
        return FailedToFindEntityException(
            entityName = JobEntry::class.java.simpleName,
            entityField = "uid",
            fieldValue = uid
        )
    }
}