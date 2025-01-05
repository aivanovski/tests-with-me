package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.testswithme.android.data.db.dao.JobHistoryDao
import com.github.aivanovski.testswithme.android.data.db.dao.StepEntryDao
import com.github.aivanovski.testswithme.android.data.file.FileCache
import com.github.aivanovski.testswithme.android.domain.dataconverters.convertToStepEntries
import com.github.aivanovski.testswithme.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.StepEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.web.api.request.PostFlowRequest
import com.github.aivanovski.testswithme.web.api.response.PostFlowResponse

class FlowRepository(
    private val stepDao: StepEntryDao,
    private val flowDao: FlowEntryDao,
    private val jobHistoryDao: JobHistoryDao,
    private val api: ApiClient,
    private val parseFlowUseCase: ParseFlowFileUseCase,
    private val fileCache: FileCache,
    private val authRepository: AuthRepository
) {

    suspend fun uploadFlowContent(
        request: PostFlowRequest
    ): Either<AppException, PostFlowResponse> =
        either {
            api.postFlow(request).bind()
        }

    fun updateCachedFlow(flow: FlowEntry) {
        flowDao.update(flow)
    }

    fun saveFlowContent(
        flowUid: String,
        content: String
    ): Either<AppException, Unit> =
        either {
            fileCache.put(flowUid, content).bind()
        }

    fun getCachedFlowContent(flowUid: String): Either<AppException, String?> =
        either {
            fileCache.getOrNull(flowUid).bind()
        }

    fun getCachedFlowByUid(flowUid: String): Either<AppException, FlowWithSteps> =
        either {
            flowDao.getByUidWithSteps(flowUid)
                ?: raise(FailedToFindEntityByUidException(FlowEntry::class, flowUid))
        }

    suspend fun getFlowContent(flowUid: String): Either<AppException, String> =
        either {
            api.getFlow(flowUid)
                .bind()
                .flow
                .base64Content
        }

    suspend fun getFlowByUid(flowUid: String): Either<AppException, FlowWithSteps> =
        either {
            val flow = flowDao.getByUidWithSteps(flowUid)
                ?: raise(FailedToFindEntityByUidException(FlowEntry::class, flowUid))

            if (flow.entry.sourceType == SourceType.REMOTE) {
                val response = api.getFlow(flowUid).bind()

                val yamlFlow = parseFlowUseCase.parseBase64File(
                    base64content = response.flow.base64Content
                ).bind()

                val stepEntries = yamlFlow.steps.convertToStepEntries(
                    flowUid = flowUid
                )

                stepDao.removeByFlowUid(flowUid)
                stepDao.insert(stepEntries)

                flowDao.getByUidWithSteps(flowUid)
                    ?: raise(FailedToFindEntityByUidException(FlowEntry::class, flowUid))
            } else {
                flow
            }
        }

    fun getStepByUid(stepUid: String): Either<AppException, StepEntry> =
        either {
            val step = stepDao.getByUid(stepUid)
                ?: raise(FailedToFindEntityByUidException(StepEntry::class, stepUid))

            step
        }

    fun removeFlowData(flowUid: String): Either<AppException, Unit> =
        either {
            // TODO: make a transaction
            flowDao.removeByUid(flowUid)
            stepDao.removeByFlowUid(flowUid)
        }

    fun save(flow: FlowWithSteps): Either<AppException, Unit> =
        either {
            val flowUid = flow.entry.uid

            removeFlowData(flowUid).bind()

            flowDao.insert(flow.entry)
            stepDao.insert(flow.steps)
        }

    suspend fun getFlows(): Either<AppException, List<FlowEntry>> =
        either {
            if (!authRepository.isUserLoggedIn()) {
                return@either flowDao.getAll()
            }

            val remoteFlows = api.getFlows().bind()

            val uidToLocalFlowMap = flowDao.getAll()
                .associateBy { flow -> flow.uid }

            for (remote in remoteFlows) {
                val local = uidToLocalFlowMap[remote.uid]
                if (local == null) {
                    flowDao.insert(remote)
                } else {
                    flowDao.update(remote.copy(id = local.id))
                }
            }

            flowDao.getAll()
        }

    suspend fun getFlowsByProjectUid(projectUid: String): Either<AppException, List<FlowEntry>> =
        either {
            getFlows().bind()
                .filter { flow -> flow.projectUid == projectUid }
        }

    suspend fun getNextStep(stepUid: String?): Either<AppException, StepEntry?> =
        either {
            val flowUid = stepUid?.let { getFlowUidByStepUid(stepUid) }
            val existingFlowEntry = flowUid?.let { flowDao.getByUid(flowUid) }

            val flow = if (existingFlowEntry == null) {
                // TODO: fix
                raise(AppException("Not implemented"))
            } else {
                flowDao.getByUidWithSteps(existingFlowEntry.uid)
                    ?: raise(
                        FailedToFindEntityByUidException(FlowEntry::class, existingFlowEntry.uid)
                    )
            }

            val currentStepEntry = stepDao.getByUid(stepUid)
                ?: raise(FailedToFindEntityByUidException(StepEntry::class, stepUid))

            val nextStepUid = currentStepEntry.nextUid

            if (nextStepUid != null) {
                val nextEntry = stepDao.getByUid(nextStepUid)
                    ?: raise(FailedToFindEntityByUidException(StepEntry::class, nextStepUid))

                nextEntry
            } else {
                null
            }
        }

    private fun getFlowUidByStepUid(stepUid: String): String? {
        return stepDao.getByUid(stepUid)?.flowUid
    }

    suspend fun removeByUid(uid: String): Either<AppException, Unit> =
        either {
            api.deleteFlow(uid).bind()
            flowDao.removeByUid(uid)
        }

    fun clear() {
        flowDao.removeAll()
        stepDao.removeAll()
    }
}