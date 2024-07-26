package com.github.aivanovski.testwithme.android.presentation.screens.flow.model

import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testwithme.android.entity.User
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry

data class FlowData(
    val project: ProjectEntry,
    val allGroups: List<Group>,
    val allRuns: List<FlowRun>,
    val group: Group?,
    val visibleFlows: List<FlowEntry>,
    val visibleRuns: List<FlowRun>,
    val allUsers: List<User>
)