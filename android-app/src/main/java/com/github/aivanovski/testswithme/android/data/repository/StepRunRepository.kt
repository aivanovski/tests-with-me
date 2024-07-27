package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.db.dao.LocalStepRunDao
import com.github.aivanovski.testswithme.android.data.db.dao.StepEntryDao
import com.github.aivanovski.testswithme.android.entity.SyncStatus
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityException
import com.github.aivanovski.testswithme.utils.StringUtils

class StepRunRepository(
    private val runDao: LocalStepRunDao,
    private val stepDao: StepEntryDao
) {

    fun getAll(): List<LocalStepRun> {
        return runDao.getAll()
    }

    fun getByJobUid(jobUid: String): Either<AppException, List<LocalStepRun>> =
        either {
            runDao.getByJobUid(jobUid)
        }

    fun getOrCreate(
        jobUid: String,
        flowUid: String,
        stepUid: String
    ): Either<AppException, LocalStepRun> =
        either {
            val entry = runDao.get(jobUid, flowUid, stepUid)

            val steps = stepDao.getByFlowUid(flowUid)
            if (steps.isEmpty()) {
                raise(AppException("No steps found"))
            }

            val isLast = (stepUid == steps.last().uid)

            val result = if (entry == null) {
                val newEntry = LocalStepRun(
                    jobUid = jobUid,
                    flowUid = flowUid,
                    stepUid = stepUid,
                    attemptCount = 0,
                    result = null,
                    isLast = isLast,
                    syncStatus = SyncStatus.NONE
                )

                runDao.insert(newEntry)

                newEntry
            } else {
                entry
            }

            result
        }

    fun add(entry: LocalStepRun) {
        runDao.insert(entry)
    }

    fun update(entry: LocalStepRun): Either<AppException, Unit> =
        either {
            val existingEntry = runDao.get(entry.jobUid, entry.flowUid, entry.stepUid)
                ?: raise(newFailedToFindEntityError())

            runDao.update(
                entry.copy(
                    id = existingEntry.id
                )
            )
        }

    private fun newFailedToFindEntityError(): FailedToFindEntityException {
        return FailedToFindEntityException(
            entityName = LocalStepRun::class.java.simpleName,
            fieldName = StringUtils.EMPTY,
            fieldValue = StringUtils.EMPTY
        )
    }
}