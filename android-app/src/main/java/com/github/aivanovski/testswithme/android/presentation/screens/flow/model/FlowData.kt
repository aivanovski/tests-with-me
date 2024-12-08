package com.github.aivanovski.testswithme.android.presentation.screens.flow.model

import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.User
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry

data class FlowData(
    val project: ProjectEntry,
    val allGroups: List<GroupEntry>,
    val allRuns: List<FlowRun>,
    val group: GroupEntry?,
    val visibleFlows: List<FlowEntry>,
    val visibleRuns: List<FlowRun>,
    val allUsers: List<User>
)