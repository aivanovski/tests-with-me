package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.db.UserEntry

data class FlowData(
    val project: ProjectEntry,
    val allGroups: List<GroupEntry>,
    val allRuns: List<FlowRunEntry>,
    val group: GroupEntry?,
    val visibleFlows: List<FlowEntry>,
    val visibleRuns: List<FlowRunEntry>,
    val allUsers: List<UserEntry>
)