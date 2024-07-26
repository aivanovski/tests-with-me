package com.github.aivanovski.testwithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.db.dao.JobDao
import com.github.aivanovski.testwithme.android.data.db.dao.JobHistoryDao
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.testwithme.android.utils.toEntry
import com.github.aivanovski.testwithme.android.utils.toHistoryEntry

class JobRepository(
    private val jobDao: JobDao,
    private val jobHistoryDao: JobHistoryDao
) {

    fun add(entry: JobEntry) {
        jobDao.insert(entry)
    }

    fun getAll(): List<JobEntry> {
        return jobDao.getAll()
    }

    fun getAllHistory(): List<JobEntry> {
        return jobHistoryDao.getAll()
            .map { it.toEntry() }
    }

    fun getJobByUid(uid: String): Either<AppException, JobEntry> = either {
        val entry = jobDao.getByUid(uid) ?: raise(newFailedToFindEntityError(uid))

        entry
    }

    fun update(job: JobEntry): Either<AppException, Unit> = either {
        val existingJob = getJobByUid(job.uid).bind()

        jobDao.update(
            job.copy(
                id = existingJob.id
            )
        )
    }

    fun removeByUid(uid: String) {
        jobDao.removeByUid(uid)
    }

    fun moveToHistory(uid: String): Either<AppException, Unit> = either {
        val job = getJobByUid(uid).bind()
        jobDao.removeByUid(uid)
        jobHistoryDao.insert(job.toHistoryEntry(id = null))
    }

    fun updateHistory(entry: JobEntry) {
        jobHistoryDao.update(entry.toHistoryEntry())
    }

    private fun newFailedToFindEntityError(
        uid: String
    ): FailedToFindEntityException {
        return FailedToFindEntityException(
            entityName = JobEntry::class.java.simpleName,
            fieldName = "uid",
            fieldValue = uid
        )
    }
}