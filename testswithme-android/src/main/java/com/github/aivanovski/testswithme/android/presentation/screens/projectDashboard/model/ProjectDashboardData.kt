package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model

import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.ExternalAppData

data class ProjectDashboardData(
    val project: ProjectEntry,
    val versions: List<AppVersion>,
    val allRuns: List<FlowRunEntry>,
    val allFlows: List<FlowEntry>,
    val allGroups: List<GroupEntry>,
    val versionRuns: List<FlowRunEntry>,
    val passedFlows: List<FlowEntry>,
    val failedFlows: List<FlowEntry>,
    val remainedFlows: List<FlowEntry>,
    val rootGroup: GroupEntry,
    val visibleGroups: List<GroupEntry>,
    val visibleFlows: List<FlowEntry>,
    val installedAppData: ExternalAppData?
)