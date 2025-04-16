package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.buildGroupTree
import com.github.aivanovski.testswithme.android.domain.findNodeByUid
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.factory.SpaceCellFactory
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ShapedSpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipItem
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipRowCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.ExternalAppData
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.GroupCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.GroupCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.model.LargeBarCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.viewModel.LargeBarCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardData
import com.github.aivanovski.testswithme.android.utils.aggregateDescendantGroupsAndFlows

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
        val spaceCellFactory = SpaceCellFactory(CellId.SECTION_SPACE_PREFIX)

        models.add(spaceCellFactory.newSpaceCell(GroupMargin))
        models.addAll(
            createApplicationInfoModels(
                project = data.project,
                installedAppData = data.installedAppData
            )
        )
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        if (data.allFlows.isNotEmpty() || data.allGroups.size > 1) {
            if (data.allFlows.isNotEmpty()) {
                models.addAll(createProgressSection(data, selectedVersion))

                if (data.remainedFlows.isNotEmpty()) {
                    models.addAll(createRemainedSection(data))
                }

                models.add(spaceCellFactory.newSpaceCell(SmallMargin))
            }

            models.addAll(createRootGroupSection(data))
            models.add(spaceCellFactory.newSpaceCell(ElementMargin))
        }

        return models
    }

    private fun createApplicationInfoModels(
        project: ProjectEntry,
        installedAppData: ExternalAppData?
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()
        val installedVersion = installedAppData?.appVersion
        val isInstalled = (installedVersion != null)

        models.add(
            LabeledTextWithIconCellModel(
                id = CellId.APPLICATION_NAME,
                label = resourceProvider.getString(R.string.application),
                text = project.name,
                icon = AppIcons.Menu,
                shape = CornersShape.TOP
            )
        )

        models.add(
            LabeledTableCellModel(
                id = CellId.APPLICATION_VERSION,
                labels = listOf(
                    resourceProvider.getString(R.string.installed_version)
                ),
                values = listOf(
                    installedAppData?.appVersion?.name
                        ?: resourceProvider.getString(R.string.not_installed)
                ),
                isClickable = false,
                shape = if (isInstalled) CornersShape.BOTTOM else CornersShape.NONE
            )
        )

        if (!isInstalled) {
            models.add(
                ButtonCellModel(
                    id = CellId.INSTALL_APPLICATION_BUTTON,
                    isButtonEnabled = true,
                    buttonColor = themeProvider.theme.colors.greenButton,
                    text = resourceProvider.getString(R.string.install_app),
                    shape = CornersShape.BOTTOM
                )
            )
        }

        return models
    }

    private fun createTitleCellModels(
        spaceCellFactory: SpaceCellFactory
    ): List<BaseCellModel> {
        return listOf(
            spaceCellFactory.newShapedSpaceModel(
                height = SmallMargin,
                shape = CornersShape.TOP
            ),
            TitleWithIconCellModel(
                id = CellId.TITLE,
                title = resourceProvider.getString(R.string.completion),
                icon = AppIcons.Menu,
                shape = CornersShape.NONE
            )
        )
    }

    private fun createVersionsCellModels(
        versions: List<AppVersion>,
        selectedVersion: String?,
        spaceCellFactory: SpaceCellFactory
    ): List<BaseCellModel> {
        if (versions.isEmpty()) {
            return emptyList()
        }

        val chips = versions.map { version ->
            val isSelected = (version.name == selectedVersion)

            TextChipItem(
                text = version.name,
                textColor = themeProvider.theme.colors.primaryText,
                textSize = TextSize.TITLE,
                isClickable = !isSelected,
                isSelected = isSelected
            )
        }

        return listOf(
            spaceCellFactory.newShapedSpaceModel(
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
        val spaceCellFactory = SpaceCellFactory(CellId.PROGRESS_SPACE_PREFIX)

        models.addAll(createTitleCellModels(spaceCellFactory))
        models.addAll(
            createVersionsCellModels(
                versions = data.versions,
                selectedVersion = selectedVersion,
                spaceCellFactory = spaceCellFactory
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
                    title = "$progressPercent%",
                    subtitle = if (progress == 1f) {
                        resourceProvider.getString(R.string.completed)
                    } else {
                        resourceProvider.getString(R.string.in_progress)
                    },
                    shape = CornersShape.NONE
                ),
                spaceCellFactory.newShapedSpaceModel(
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
                    isClickable = true,
                    shape = CornersShape.NONE
                ),
                spaceCellFactory.newShapedSpaceModel(
                    height = SmallMargin,
                    shape = CornersShape.BOTTOM
                )
            )
        )

        return models
    }

    private fun createRemainedSection(data: ProjectDashboardData): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()
        val spaceCellFactory = SpaceCellFactory(CellId.REMAINED_SPACE_PREFIX)

        models.add(spaceCellFactory.newSpaceCell(SmallMargin))
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
                flows = visibleRemainedFlows,
                spaceCellFactory = spaceCellFactory
            )
        )

        return models
    }

    private fun createRootGroupSection(data: ProjectDashboardData): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()
        val spaceCellFactory = SpaceCellFactory(CellId.GROUP_SPACE_PREFIX)

        models.add(
            HeaderCellModel(
                id = CellId.GROUPS_HEADER,
                title = resourceProvider.getString(R.string.tests),
                iconText = resourceProvider.getString(R.string.view),
                icon = AppIcons.ArrowForward
            )
        )

        val groupTree = data.allGroups.buildGroupTree()
        val uidToGroupMap = data.allGroups.associateBy { group -> group.uid }

        val visibleGroups = groupTree.nodes
            .mapNotNull { node -> uidToGroupMap[node.entity.uid] }

        for ((index, group) in visibleGroups.withIndex()) {
            if (index > 0) {
                models.add(spaceCellFactory.newSpaceCell(SmallMargin))
            }

            val groupNode = groupTree.findNodeByUid(group.uid)
            val (_, descendantFlows) = groupNode?.aggregateDescendantGroupsAndFlows(
                groups = data.allGroups,
                flows = data.allFlows
            )
                ?: (emptyList<GroupEntry>() to emptyList())

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

        return models
    }

    private fun createRemainedFlowCellModels(
        flows: List<FlowEntry>,
        spaceCellFactory: SpaceCellFactory
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        for ((index, flow) in flows.withIndex()) {
            if (index > 0) {
                models.add(spaceCellFactory.newSpaceCell(SmallMargin))
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

    object CellId {
        const val APPLICATION_NAME = "application-name"
        const val APPLICATION_VERSION = "application-version"
        const val INSTALL_APPLICATION_BUTTON = "install-application-button"
        const val TITLE = "title"
        const val PROGRESS = "progress"
        const val STATS_TABLE = "stats-table"
        const val VERSIONS = "versions"
        const val REMAINED_FLOWS_HEADER = "remained-flows-header"
        const val GROUPS_HEADER = "groups-header"

        const val SECTION_SPACE_PREFIX = "section_space_"
        const val REMAINED_SPACE_PREFIX = "remained_space_"
        const val GROUP_SPACE_PREFIX = "group_space_"
        const val PROGRESS_SPACE_PREFIX = "progress_space_"
    }

    companion object {
        private const val MAX_VISIBLE_ELEMENTS = 5
    }
}