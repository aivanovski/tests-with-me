package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

import arrow.core.Option
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.db.StepEntry
import com.github.aivanovski.testswithme.android.entity.db.UserEntry

data class FlowData(
    val project: ProjectEntry?,
    val allGroups: List<GroupEntry>,
    val group: GroupEntry?,
    val visibleFlows: List<FlowEntry>,
    val visibleRuns: List<FlowRunEntry>,
    val visibleJobs: List<JobEntry>,
    val allUsers: List<UserEntry>,
    val user: UserEntry,
    val steps: Option<List<StepEntry>>
)