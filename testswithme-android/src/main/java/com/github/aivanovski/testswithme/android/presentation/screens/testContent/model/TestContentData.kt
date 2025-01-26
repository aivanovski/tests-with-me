package com.github.aivanovski.testswithme.android.presentation.screens.testContent.model

import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.flow.runner.report.model.ReportItem

data class TestContentData(
    val flow: FlowWithSteps,
    val localRuns: List<LocalStepRun>,
    val job: JobEntry?,
    val remoteRun: FlowRunEntry?,
    val report: String?,
    val parsedReport: ReportItem.FlowItem?
)