package com.github.aivanovski.testwithme.android.presentation.screens.testRuns.model

import com.github.aivanovski.testwithme.android.entity.FlowWithSteps
import com.github.aivanovski.testwithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.db.LocalStepRun

data class TestRunsData(
    val allProjects: List<ProjectEntry>,
    val allFlows: List<FlowWithSteps>,
    val jobHistory: List<JobEntry>,
    val localRuns: List<LocalStepRun>
)