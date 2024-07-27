package com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model

import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry

data class TestRunsData(
    val allProjects: List<ProjectEntry>,
    val allFlows: List<FlowWithSteps>,
    val jobHistory: List<JobEntry>,
    val localRuns: List<LocalStepRun>
)