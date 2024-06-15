package com.github.aivanovski.testwithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.db.dao.ExecutionDataDao
import com.github.aivanovski.testwithme.android.entity.db.ExecutionData
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.testwithme.android.utils.StringUtils

class ExecutionDataRepository(
    private val dao: ExecutionDataDao
) {

    fun getOrCreate(
        jobUid: String,
        flowUid: String,
        stepUid: String
    ): Either<AppException, ExecutionData> = either {
        val entry = dao.get(jobUid, flowUid, stepUid)

        val result = if (entry == null) {
            val newEntry = ExecutionData(
                jobUid = jobUid,
                flowUid = flowUid,
                stepUid = stepUid,
                attemptCount = 0,
                result = null
            )

            dao.insert(newEntry)

            newEntry
        } else {
            entry
        }

        result
    }

    fun add(entry: ExecutionData) {
        dao.insert(entry)
    }

    fun update(
        entry: ExecutionData
    ): Either<AppException, Unit> = either {
        val existingEntry = dao.get(entry.jobUid, entry.flowUid, entry.stepUid)
            ?: raise(newFailedToFindEntityError())

        dao.update(
            entry.copy(
                id = existingEntry.id
            )
        )
    }

    private fun newFailedToFindEntityError(): FailedToFindEntityException {
        return FailedToFindEntityException(
            entityName = ExecutionData::class.java.simpleName,
            entityField = StringUtils.EMPTY,
            fieldValue = StringUtils.EMPTY
        )
    }
}