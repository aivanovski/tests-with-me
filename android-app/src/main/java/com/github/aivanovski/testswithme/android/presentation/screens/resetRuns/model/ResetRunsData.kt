package com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model

import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry

data class ResetRunsData(
    val project: ProjectEntry,
    val versionNames: List<String>,
    val flows: List<FlowEntry>
)