package com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.cells

import com.github.aivanovski.testwithme.android.R
import com.github.aivanovski.testwithme.android.domain.buildGroupTree
import com.github.aivanovski.testwithme.android.domain.findNodeByUid
import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.entity.FlowRun
import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.AppVersion
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconTextCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.LabeledTableCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.ShapedSpaceCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextChipItem
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextChipRowCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.TitleCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.model.GroupCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.groups.cells.viewModel.GroupCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.cells.model.LargeBarCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.cells.viewModel.LargeBarCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.model.ProjectDashboardData
import com.github.aivanovski.testwithme.android.utils.aggregateDescendantGroupsAndFlows
import com.github.aivanovski.testwithme.android.utils.aggregateLastRunByFlowUid
import com.github.aivanovski.testwithme.android.utils.aggregateRunCountByFlowUid
import com.github.aivanovski.testwithme.android.utils.formatRunTime
import com.github.aivanovski.testwithme.utils.StringUtils

class ProjectDashboardCellFactory(
    private val resourceProvider: ResourceProvider,
    private val themeProvider: ThemeProvider
) {

    fun createCellViewModels(
        data: ProjectDashboardData,
        selectedVersion: String?,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createCellModels(
            data = data,
            selectedVersion = selectedVersion
        ).map { model ->
            when (model) {
                is LargeBarCellModel -> LargeBarCellViewModel(model)
                is FlowCellModel -> FlowCellViewModel(model, intentProvider)
                is GroupCellModel -> GroupCellViewModel(model, intentProvider)
                else -> createCoreCellViewModel(model, intentProvider)
            }
        }
    }

    private fun createCellModels(
        data: ProjectDashboardData,
        selectedVersion: String?
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        if (data.allFlows.isNotEmpty()) {
            models.add(SpaceCellModel(ElementMargin))
            models.addAll(createProgressSection(data, selectedVersion))

            if (data.remainedFlows.isNotEmpty()) {
                models.addAll(createRemainedSection(data))
            }

            models.addAll(createRootGroupSection(data))
            models.add(SpaceCellModel(ElementMargin))
        } else {

        }

        return models
    }

    private fun createTitleCellModels(): List<BaseCellModel> {
        return listOf(
            ShapedSpaceCellModel(
                height = SmallMargin,
                shape = CornersShape.TOP
            ),
            TitleCellModel(
                id = CellId.TITLE,
                title = resourceProvider.getString(R.string.completion),
                shape = CornersShape.NONE
            )
        )
    }

    private fun createVersionsCellModels(
        versions: List<AppVersion>,
        selectedVersion: String?
    ): List<BaseCellModel> {
        if (versions.isEmpty()) {
            return emptyList()
        }

        val chips = versions.map { version ->
            val isSelected = (version.name == selectedVersion)

            TextChipItem(
                text = version.name,
                textColor = themeProvider.theme.colors.primaryText,
                textSize = TextSize.LARGE,
                isClickable = !isSelected,
                isSelected = isSelected
            )
        }

        return listOf(
            ShapedSpaceCellModel(
                height = SmallMargin,
                shape = CornersShape.NONE
            ),
            TextChipRowCellModel(
                id = CellId.VERSIONS,
                chips = chips,
                shape = CornersShape.NONE
            )
        )
    }

    private fun createProgressSection(
        data: ProjectDashboardData,
        selectedVersion: String?
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.addAll(createTitleCellModels())
        models.addAll(
            createVersionsCellModels(
                versions = data.versions,
                selectedVersion = selectedVersion
            )
        )

        val progress = if (data.remainedFlows.isEmpty()) {
            1f
        } else {
            (data.allFlows.size - data.remainedFlows.size).toFloat() / data.allFlows.size
        }
        val progressPercent = (progress * 100).toInt()

        models.addAll(
            listOf(
                LargeBarCellModel(
                    id = CellId.PROGRESS,
                    progress = progress,
                    title = "${progressPercent}%",
                    subtitle = if (progress == 1f) {
                        resourceProvider.getString(R.string.completed)
                    } else {
                        resourceProvider.getString(R.string.in_progress)
                    },
                    shape = CornersShape.NONE
                ),
                ShapedSpaceCellModel(
                    height = ElementMargin,
                    shape = CornersShape.NONE
                ),
                LabeledTableCellModel(
                    id = CellId.STATS_TABLE,
                    labels = listOf(
                        resourceProvider.getString(R.string.passed),
                        resourceProvider.getString(R.string.failed),
                        resourceProvider.getString(R.string.remained)
                    ),
                    values = listOf(
                        data.passedFlows.size.toString(),
                        data.failedFlows.size.toString(),
                        data.remainedFlows.size.toString()
                    ),
                    shape = CornersShape.NONE
                ),
                ShapedSpaceCellModel(
                    height = SmallMargin,
                    shape = CornersShape.BOTTOM
                )
            )
        )

        return models
    }

    private fun createRemainedSection(
        data: ProjectDashboardData
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(SpaceCellModel(height = SmallMargin))
        models.add(
            HeaderCellModel(
                id = CellId.REMAINED_FLOWS_HEADER,
                title = resourceProvider.getString(R.string.remained_tests),
                iconText = resourceProvider.getString(R.string.view),
                icon = AppIcons.ArrowForward
            )
        )

        val visibleRemainedFlows = data.remainedFlows.take(MAX_VISIBLE_ELEMENTS)
        models.addAll(
            createRemainedFlowCellModels(
                flows = visibleRemainedFlows
            )
        )

        return models
    }

    private fun createRootGroupSection(
        data: ProjectDashboardData
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(SpaceCellModel(height = SmallMargin))
        models.add(
            HeaderCellModel(
                id = CellId.GROUPS_HEADER,
                title = resourceProvider.getString(R.string.tests),
                iconText = resourceProvider.getString(R.string.view),
                icon = AppIcons.ArrowForward
            )
        )

        val visibleGroups = data.rootGroups.take(MAX_VISIBLE_ELEMENTS)
        val groupTree = data.allGroups.buildGroupTree()

        for ((index, group) in visibleGroups.withIndex()) {
            if (index > 0) {
                models.add(SpaceCellModel(SmallMargin))
            }

            val groupNode = groupTree.findNodeByUid(group.uid)
            val (_, descendantFlows) = groupNode?.aggregateDescendantGroupsAndFlows(
                groups = data.allGroups,
                flows = data.allFlows
            )
                ?: (emptyList<Group>() to emptyList())

            models.add(
                GroupCellModel(
                    id = group.uid,
                    icon = AppIcons.Folder,
                    iconTint = IconTint.PRIMARY_ICON,
                    title = group.name,
                    chips = listOf(
                        resourceProvider.getString(
                            R.string.tests_with_number,
                            descendantFlows.size
                        )
                    )
                )
            )
        }

        if (visibleGroups.size < MAX_VISIBLE_ELEMENTS) {
            if (visibleGroups.isNotEmpty()) {
                models.add(SpaceCellModel(SmallMargin))
            }

            val visibleFlows = data.rootFlows.take(MAX_VISIBLE_ELEMENTS - visibleGroups.size)

            models.addAll(
                createFlowCellModels(
                    flows = visibleFlows,
                    allRuns = data.allRuns
                )
            )
        }

        return models
    }

    private fun createRemainedFlowCellModels(
        flows: List<FlowEntry>
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        for ((index, flow) in flows.withIndex()) {
            if (index > 0) {
                models.add(SpaceCellModel(SmallMargin))
            }

            models.add(
                IconTextCellModel(
                    id = flow.uid,
                    title = flow.name,
                    icon = AppIcons.CheckCircle,
                    iconTint = IconTint.PRIMARY_ICON
                )
            )
        }

        return models
    }

    private fun createFlowCellModels(
        flows: List<FlowEntry>,
        allRuns: List<FlowRun>
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()
        val flowUidToLastRunMap = allRuns.aggregateLastRunByFlowUid()
        val flowUidToRunCountMap = allRuns.aggregateRunCountByFlowUid()

        for ((index, flow) in flows.withIndex()) {
            if (index > 0) {
                models.add(SpaceCellModel(SmallMargin))
            }

            val lastRun = flowUidToLastRunMap[flow.uid]
            val runCount = flowUidToRunCountMap[flow.uid] ?: 0

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
                    title = flow.name,
                    icon = icon,
                    iconTint = iconTint,
                    description = lastRun?.finishedAt?.formatRunTime(resourceProvider)
                        ?: StringUtils.EMPTY,
                    chipText = resourceProvider.getString(R.string.runs_with_number, runCount)
                )
            )
        }

        return models
    }

    object CellId {
        const val TITLE = "title"
        const val PROGRESS = "progress"
        const val STATS_TABLE = "stats-table"
        const val VERSIONS = "versions"
        const val REMAINED_FLOWS_HEADER = "remained-flows-header"
        const val GROUPS_HEADER = "groups-header"
    }

    companion object {
        private const val MAX_VISIBLE_ELEMENTS = 5
    }
}