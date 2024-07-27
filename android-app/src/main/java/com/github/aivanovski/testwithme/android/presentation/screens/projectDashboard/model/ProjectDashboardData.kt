package com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.model

import com.github.aivanovski.testwithme.android.entity.AppVersion
import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry

data class ProjectDashboardData(
    val versions: List<AppVersion>,
    val allRuns: List<FlowRun>,
    val allFlows: List<FlowEntry>,
    val allGroups: List<Group>,
    val versionRuns: List<FlowRun>,
    val passedFlows: List<FlowEntry>,
    val failedFlows: List<FlowEntry>,
    val remainedFlows: List<FlowEntry>,
    val rootGroups: List<Group>,
    val rootFlows: List<FlowEntry>
)