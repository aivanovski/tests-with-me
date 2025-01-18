package com.github.aivanovski.testswithme.android.presentation.screens.groups.model

import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry

data class GroupsData(
    val allGroups: List<GroupEntry>,
    val group: GroupEntry?,
    val groups: List<GroupEntry>,
    val allFlows: List<FlowEntry>,
    val flows: List<FlowEntry>,
    val allRuns: List<FlowRunEntry>,
    val runs: List<FlowRunEntry>
)