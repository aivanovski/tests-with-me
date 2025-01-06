package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.db.dao.FlowRunEntryDao
import com.github.aivanovski.testswithme.android.entity.FlowRunUploadResult
import com.github.aivanovski.testswithme.android.entity.FlowRunWithReport
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.web.api.request.PostFlowRunRequest
import com.github.aivanovski.testswithme.web.api.request.ResetFlowRunsRequest
import com.github.aivanovski.testswithme.web.api.response.ResetFlowRunsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class FlowRunRepository(
    private val flowRunDao: FlowRunEntryDao,
    private val api: ApiClient,
    private val authRepository: AuthRepository
) {

    fun getRunsFlow(): Flow<Either<AppException, List<FlowRunEntry>>> =
        flow {
            val localRuns = flowRunDao.getAll()

            if (!authRepository.isUserLoggedIn()) {
                emit(localRuns.right())
                return@flow
            }

            if (localRuns.isNotEmpty()) {
                emit(localRuns.right())
            }

            val getRemoteRuns = api.getFlowRuns()
            if (getRemoteRuns.isLeft()) {
                if (localRuns.isEmpty()) {
                    emit(getRemoteRuns)
                }
                return@flow
            }

            val remoteRuns = getRemoteRuns.unwrap()

            mergeEntities(
                localEntities = localRuns,
                remoteEntities = remoteRuns,
                entityToUidMapper = { run -> run.uid },
                onInsert = { run -> flowRunDao.insert(run) },
                onUpdate = { local, remote -> flowRunDao.update(remote.copy(id = local.id)) },
                onDelete = { run -> flowRunDao.removeByUid(run.uid) }
            )

            emit(flowRunDao.getAll().right())
        }
            .distinctUntilChanged()

    suspend fun getRuns(): Either<AppException, List<FlowRunEntry>> = api.getFlowRuns()

    suspend fun getRun(runUid: String): Either<AppException, FlowRunWithReport> =
        api.getFlowRun(runUid)

    suspend fun uploadRun(run: PostFlowRunRequest): Either<AppException, FlowRunUploadResult> =
        either {
            val response = api.postFlowRun(run).bind()

            FlowRunUploadResult(
                uid = response.id,
                isAccepted = response.isAccepted
            )
        }

    suspend fun resetRuns(
        projectUid: String,
        versionName: String
    ): Either<AppException, ResetFlowRunsResponse> =
        either {
            val request = ResetFlowRunsRequest(
                projectId = projectUid,
                versionName = versionName
            )

            api.resetFlowRun(request).bind()
        }
}