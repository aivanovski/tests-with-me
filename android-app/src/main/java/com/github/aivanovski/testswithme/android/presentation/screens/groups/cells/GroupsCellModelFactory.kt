package com.github.aivanovski.testswithme.android.presentation.screens.groups.cells

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.buildGroupTree
import com.github.aivanovski.testswithme.android.domain.findNodeByUid
import com.github.aivanovski.testswithme.android.domain.getDescendantNodes
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.GroupCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsData
import com.github.aivanovski.testswithme.android.utils.aggregateByFlowUid
import com.github.aivanovski.testswithme.android.utils.formatRunTime
import com.github.aivanovski.testswithme.utils.StringUtils

class GroupsCellModelFactory(
    private val resourceProvider: ResourceProvider
) {

    fun createCellViewModels(
        data: GroupsData,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createCellModels(data).map { model ->
            when (model) {
                is FlowCellModel -> FlowCellViewModel(model, intentProvider)
                is GroupCellModel -> GroupCellViewModel(model, intentProvider)
                is SpaceCellModel -> SpaceCellViewModel(model)
                else -> createCoreCellViewModel(model, intentProvider)
            }
        }
    }

    fun createCellModels(data: GroupsData): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        if (data.groups.isNotEmpty() || data.flows.isNotEmpty()) {
            models.add(SpaceCellModel(ElementMargin))
        }

        val groupTree = data.allGroups.buildGroupTree()

        for ((index, group) in data.groups.withIndex()) {
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

            val flowUids = data.allFlows
                .filter { flow -> flow.groupUid in groupUids }
                .map { flow -> flow.uid }
                .toSet()

            val groupFlows = data.allFlows
                .filter { flow -> flow.uid in flowUids }

            val groupRuns = data.allRuns
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

        val flowUidToRunsMap = data.runs.aggregateByFlowUid()

        for ((index, flow) in data.flows.withIndex()) {
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