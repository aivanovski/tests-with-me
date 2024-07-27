package com.github.aivanovski.testswithme.android.presentation.screens.groups.model

import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry

data class GroupsData(
    val allGroups: List<Group>,
    val group: Group?,
    val groups: List<Group>,
    val allFlows: List<FlowEntry>,
    val flows: List<FlowEntry>,
    val allRuns: List<FlowRun>,
    val runs: List<FlowRun>
)