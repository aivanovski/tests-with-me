package com.github.aivanovski.testwithme.android.presentation.screens.testRun.model

import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.JobEntry

data class TestRunScreenData(
    val job: JobEntry,
    val flow: FlowEntry,
    val flowContent: String
)