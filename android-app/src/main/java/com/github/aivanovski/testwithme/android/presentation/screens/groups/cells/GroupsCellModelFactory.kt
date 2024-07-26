package com.github.aivanovski.testwithme.android.presentation.screens.groups.cells

import com.github.aivanovski.testwithme.android.R
import com.github.aivanovski.testwithme.android.domain.buildGroupTree
import com.github.aivanovski.testwithme.android.domain.findNodeByUid
import com.github.aivanovski.testwithme.android.domain.getDescendantNodes
import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model.GroupCellModel
import com.github.aivanovski.testwithme.android.utils.aggregateByFlowUid
import com.github.aivanovski.testwithme.android.utils.formatRunTime
import com.github.aivanovski.testwithme.utils.StringUtils

class GroupsCellModelFactory(
    private val resourceProvider: ResourceProvider
) {

    fun createCellModels(
        allGroup: List<Group>,
        groups: List<Group>,
        allFlows: List<FlowEntry>,
        flows: List<FlowEntry>,
        allRuns: List<FlowRun>,
        runs: List<FlowRun>
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        if (groups.isNotEmpty() || flows.isNotEmpty()) {
            models.add(SpaceCellModel(ElementMargin))
        }

        val groupTree = allGroup.buildGroupTree()

        for ((index, group) in groups.withIndex()) {
            if (index > 0) {
                models.add(SpaceCellModel(SmallMargin))
            }

            val groupNode = groupTree.findNodeByUid(group.uid)
            val descendantGroups = groupNode?.getDescendantNodes() ?: emptyList()

            val groupUids = mutableListOf<String>()
                .apply {
                    add(group.uid)

                    descendantGroups.forEach { descendant ->
                        add(descendant.entity.uid)
                    }
                }
                .toSet()

            val flowUids = allFlows
                .filter { flow -> flow.groupUid in groupUids }
                .map { flow -> flow.uid }
                .toSet()

            val groupFlows = allFlows
                .filter { flow -> flow.uid in flowUids }

            val groupRuns = allRuns
                .filter { run -> run.flowUid in flowUids }

            models.add(
                GroupCellModel(
                    id = group.uid,
                    icon = AppIcons.Folder,
                    iconTint = IconTint.PRIMARY_ICON,
                    title = group.name,
                    chips = listOf(
                        resourceProvider.getString(R.string.tests_with_number, groupFlows.size),
                        resourceProvider.getString(R.string.runs_with_number, groupRuns.size)
                    )
                )
            )
        }

        val flowUidToRunsMap = runs.aggregateByFlowUid()

        for ((index, flow) in flows.withIndex()) {
            if (models.size > 0) {
                models.add(SpaceCellModel(SmallMargin))
            }

            val flowRuns = flowUidToRunsMap[flow.uid] ?: emptyList()

            val lastRun = flowRuns.firstOrNull()

            val icon = when {
                lastRun?.isSuccess == false -> AppIcons.ErrorCircle
                else -> AppIcons.CheckCircle
            }

            val iconTint = when {
                lastRun == null -> IconTint.PRIMARY_ICON
                lastRun.isSuccess -> IconTint.GREEN
                else -> IconTint.RED
            }

            models.add(
                FlowCellModel(
                    id = flow.uid,
                    icon = icon,
                    iconTint = iconTint,
                    title = flow.name,
                    description = lastRun?.finishedAt?.formatRunTime(resourceProvider)
                        ?: StringUtils.EMPTY,
                    chipText = resourceProvider.getString(R.string.runs_with_number, flowRuns.size)
                )
            )
        }

        return models
    }
}