package com.github.aivanovski.testswithme.android.presentation.screens.testReport

import arrow.core.Either
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.entity.FlowRunWithReport
import com.github.aivanovski.testswithme.android.entity.exception.AppException

class TestReportInteractor(
    private val flowRunRepository: FlowRunRepository
) {

    suspend fun loadData(flowRunUid: String): Either<AppException, FlowRunWithReport> =
        flowRunRepository.getRun(flowRunUid)
}