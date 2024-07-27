package com.github.aivanovski.testswithme.android.presentation.screens.testRun

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.db.JobHistoryEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunScreenData
import com.github.aivanovski.testswithme.android.utils.Base64Utils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class TestRunInteractor(
    private val jobRepository: JobRepository,
    private val flowRepository: FlowRepository
) {

    suspend fun loadData(jobUid: String): Either<AppException, TestRunScreenData> =
        withContext(IO) {
            either {
                val job = jobRepository.getAllHistory()
                    .firstOrNull { job -> job.uid == jobUid }
                    ?: raise(FailedToFindEntityByUidException(JobHistoryEntry::class, jobUid))

                val flowUid = job.flowUid
                val flow = flowRepository.getCachedFlowByUid(flowUid).bind()

                val content = when (flow.entry.sourceType) {
                    SourceType.REMOTE -> {
                        flowRepository.getFlowContent(flowUid)
                            .bind()
                            .let { base64Content -> Base64Utils.decode(base64Content) }
                    }

                    SourceType.LOCAL -> {
                        flowRepository.getCachedFlowContent(flowUid)
                            .bind()
                    }
                } ?: raise(AppException("Failed to get flow content: uid=$flowUid"))

                TestRunScreenData(
                    job = job,
                    flow = flow.entry,
                    flowContent = content
                )
            }
        }
}