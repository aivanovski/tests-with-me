package com.github.aivanovski.testswithme.android.presentation.screens.testRun

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.db.JobHistoryEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunScreenData
import com.github.aivanovski.testswithme.utils.Base64Utils

class TestRunInteractor(
    private val jobRepository: JobRepository,
    private val flowRepository: FlowRepository,
    private val authRepository: AuthRepository
) {

    fun isLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun isLoggedInFlow() = authRepository.isLoggedInFlow()

    suspend fun loadData(jobUid: String): Either<AppException, TestRunScreenData> =
        either {
            val job = jobRepository.getAllHistory()
                .firstOrNull { job -> job.uid == jobUid }
                ?: raise(FailedToFindEntityByUidException(JobHistoryEntry::class, jobUid))

            val flowUid = job.flowUid
            val flow = flowRepository.getCachedFlowByUid(flowUid).bind()

            val isRemote =
                (flow.entry.sourceType == SourceType.REMOTE && authRepository.isUserLoggedIn())

            val content = if (isRemote) {
                val base64Content = flowRepository.getFlowContent(flowUid).bind()

                Base64Utils.decode(base64Content)
                    .mapLeft { exception -> AppException(cause = exception) }
                    .bind()
            } else {
                flowRepository.getCachedFlowContent(flowUid).bind()
                    ?: raise(AppException("Failed to get flow content: uid=$flowUid"))
            }

            TestRunScreenData(
                job = job,
                flow = flow.entry,
                flowContent = content
            )
        }
}