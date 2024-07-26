package com.github.aivanovski.testwithme.android.presentation.screens.groups.model

import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry

data class GroupsData(
    val allGroups: List<Group>,
    val group: Group?,
    val groups: List<Group>,
    val allFlows: List<FlowEntry>,
    val flows: List<FlowEntry>,
    val allRuns: List<FlowRun>,
    val runs: List<FlowRun>
)