package com.github.aivanovski.testswithme.android.presentation.screens.testRun.model

import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.JobEntry

data class TestRunScreenData(
    val job: JobEntry,
    val flow: FlowEntry,
    val flowContent: String
)