package com.github.aivanovski.testwithme.android.data.repository

import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.api.ApiClient
import com.github.aivanovski.testwithme.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.testwithme.android.data.db.dao.StepEntryDao
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.StepEntry
import com.github.aivanovski.testwithme.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.testwithme.android.entity.FlowWithSteps
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FailedToFindEntityException
import arrow.core.Either

class FlowRepositoryImpl(
    private val stepDao: StepEntryDao,
    private val flowDao: FlowEntryDao,
    private val api: ApiClient,
    private val parseFlowUseCase: ParseFlowFileUseCase
) : FlowRepository {

    override suspend fun findStepByUid(
        uid: String
    ): Either<AppException, StepEntry?> = either {
        val step = stepDao.getAll()
            .firstOrNull { step -> step.uid == uid }

        step
    }

    override suspend fun getFlowByUid(
        flowUid: String
    ): Either<AppException, FlowWithSteps> = either {
        val flow = flowDao.getByUidWithSteps(flowUid)
            ?: raise(newUnableToFindFlowByUidError(flowUid))

        flow
    }

    override suspend fun getStepByUid(
        stepUid: String
    ): Either<AppException, StepEntry> = either {
        val step = stepDao.getByUid(stepUid)
            ?: raise(newUnableToFindStepByUidError(stepUid))

        step
    }

    override suspend fun removeFlowData(
        flowUid: String
    ): Either<AppException, Unit> = either {
        // TODO: make a transaction
        flowDao.removeByUid(flowUid)
        stepDao.removeByFlowUid(flowUid)
    }

    override suspend fun save(
        flow: FlowWithSteps
    ): Either<AppException, Unit> = either {
        val flowUid = flow.entry.uid

        removeFlowData(flowUid).bind()

        flowDao.insert(flow.entry)
        stepDao.insert(flow.steps)
    }

    override suspend fun getNextStep(
        stepUid: String?
    ): Either<AppException, StepEntry?> = either {
        val flowUid = stepUid?.let { getFlowUidByStepUid(stepUid) }
        val existingFlowEntry = flowUid?.let { flowDao.getByUid(flowUid) }

        val flow = if (existingFlowEntry == null) {
            if (flowUid == null) {
                raise(AppException("Flow uid is null"))
            }

            val response = api.getFlow(flowUid).bind()

            val flowEntry = parseFlowUseCase.parseBase64File(
                base64content = response.flow.base64Content
            ).bind()

            flowDao.insert(flowEntry.entry)
            stepDao.insert(flowEntry.steps)

            flowEntry
        } else {
            flowDao.getByUidWithSteps(existingFlowEntry.uid)
                ?: raise(newUnableToFindFlowByUidError(existingFlowEntry.uid))
        }

        val currentStepEntry = stepDao.getByUid(stepUid)
            ?: raise(newUnableToFindStepByUidError(stepUid))

        val nextStepUid = currentStepEntry.nextUid

        if (nextStepUid != null) {
            val nextEntry = stepDao.getByUid(nextStepUid)
                ?: raise(newUnableToFindStepByUidError(nextStepUid))

            nextEntry
        } else {
            null
        }
    }

    private fun getFlowUidByStepUid(stepUid: String): String? {
        return stepDao.getByUid(stepUid)?.flowUid
    }

    override suspend fun updateStep(
        stepEntry: StepEntry
    ): Either<AppException, Unit> {
        val existingEntry = stepDao.getByUid(stepEntry.uid)
            ?: return Either.Left(newUnableToFindStepByUidError(stepEntry.uid))

        stepDao.update(stepEntry.copy(id = existingEntry.id))

        return Either.Right(Unit)
    }

    override suspend fun updateFlow(
        flowEntry: FlowEntry
    ): Either<AppException, Unit> {
        val existingEntry = flowDao.getByUid(flowEntry.uid)
            ?: return Either.Left(newUnableToFindFlowByUidError(flowEntry.uid))

        flowDao.update(flowEntry.copy(id = existingEntry.id))

        return Either.Right(Unit)
    }

    private fun newUnableToFindFlowByUidError(uid: String): AppException {
        return FailedToFindEntityException(
            entityName = FlowEntry::class.java.simpleName,
            entityField = "uid",
            fieldValue = uid
        )
    }

    private fun newUnableToFindStepByUidError(uid: String): AppException {
        return FailedToFindEntityException(
            entityName = StepEntry::class.java.simpleName,
            entityField = "uid",
            fieldValue = uid
        )
    }
}