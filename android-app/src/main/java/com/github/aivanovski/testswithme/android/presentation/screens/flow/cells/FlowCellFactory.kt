package com.github.aivanovski.testswithme.android.presentation.screens.flow.cells

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.db.UserEntry
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.EmptyTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.model.HistoryItemCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.viewModel.HistoryItemCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.ExternalAppData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowData
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.utils.aggregateByFlowUid
import com.github.aivanovski.testswithme.android.utils.findParentGroups
import com.github.aivanovski.testswithme.android.utils.formatRunTime
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.utils.StringUtils.DASH
import com.github.aivanovski.testswithme.utils.StringUtils.SLASH
import com.github.aivanovski.testswithme.utils.StringUtils.SPACE

class FlowCellFactory(
    private val resourceProvider: ResourceProvider,
    private val themeProvider: ThemeProvider
) {

    private fun List<BaseCellModel>.toViewModels(
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return this.map { model ->
            when (model) {
                is HistoryItemCellModel -> HistoryItemCellViewModel(model, intentProvider)
                is FlowCellModel -> FlowCellViewModel(model, intentProvider)
                else -> createCoreCellViewModel(model, intentProvider)
            }
        }
    }

    fun createGroupCellViewModels(
        data: FlowData,
        installedAppData: ExternalAppData?,
        isDriverRunning: Boolean,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        val group = data.group ?: return emptyList()

        val models = mutableListOf<BaseCellModel>()

        val isAppInstalled = (installedAppData != null)

        models.add(SpaceCellModel(height = ElementMargin))

        models.addAll(createDriverStatusModels(isDriverRunning))
        models.add(newElementSpaceModel())

        models.addAll(
            createAppInfoModels(
                project = data.project,
                requiredAppVersion = null,
                installedAppData = installedAppData
            )
        )
        models.add(newElementSpaceModel())

        val parents = findParentGroups(group, data.allGroups)

        models.addAll(
            createGroupTitleModels(
                title = formatGroupPath(parents.plus(group)),
                isDriverRunning = isDriverRunning,
                isAppInstalled = isAppInstalled
            )
        )
        models.add(newElementSpaceModel())

        if (data.visibleFlows.isNotEmpty()) {
            models.addAll(createStatsModels(data.visibleRuns))
            models.addAll(createTestListModels(data.visibleFlows, data.visibleRuns))
        } else {
            models.add(
                EmptyTextCellModel(
                    id = CellId.EMPTY_MESSAGE,
                    message = resourceProvider.getString(R.string.no_tests)
                )
            )
        }

        return models.toViewModels(intentProvider)
    }

    fun createRemainedFlowsCellViewModels(
        data: FlowData,
        requiredAppVersion: AppVersion?,
        installedAppData: ExternalAppData?,
        isDriverRunning: Boolean,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        val models = mutableListOf<BaseCellModel>()

        val installedVersion = installedAppData?.appVersion
        val isAppInstalled = (
            requiredAppVersion == null ||
                (installedVersion != null && installedVersion.name == requiredAppVersion.name)
            )

        models.add(newElementSpaceModel())
        models.addAll(createDriverStatusModels(isDriverRunning))
        models.add(newElementSpaceModel())

        models.addAll(
            createAppInfoModels(
                project = data.project,
                requiredAppVersion = requiredAppVersion,
                installedAppData = installedAppData
            )
        )
        models.add(newElementSpaceModel())

        models.addAll(
            createGroupTitleModels(
                title = resourceProvider.getString(R.string.remained_tests),
                isDriverRunning = isDriverRunning,
                isAppInstalled = isAppInstalled
            )
        )

        models.add(newElementSpaceModel())

        if (data.visibleFlows.isNotEmpty()) {
            models.addAll(createTestListModels(data.visibleFlows, data.visibleRuns))
        } else {
            models.add(
                EmptyTextCellModel(
                    id = CellId.EMPTY_MESSAGE,
                    message = resourceProvider.getString(R.string.no_tests)
                )
            )
        }

        return models.toViewModels(intentProvider)
    }

    fun createFlowCellViewModels(
        data: FlowData,
        requiredAppVersion: AppVersion?,
        installedAppData: ExternalAppData?,
        isDriverRunning: Boolean,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        val flow = data.visibleFlows.firstOrNull() ?: return emptyList()

        val models = mutableListOf<BaseCellModel>()

        val isAppInstalled = (installedAppData != null)

        models.add(newElementSpaceModel())
        models.addAll(createDriverStatusModels(isDriverRunning))
        models.add(newElementSpaceModel())

        models.addAll(
            createAppInfoModels(
                project = data.project,
                requiredAppVersion = requiredAppVersion,
                installedAppData = installedAppData
            )
        )
        models.add(newElementSpaceModel())

        models.addAll(
            createTestTitleModels(
                flow = flow,
                isDriverRunning = isDriverRunning,
                isAppInstalled = isAppInstalled
            )
        )
        models.add(newElementSpaceModel())

        if (data.visibleRuns.isNotEmpty()) {
            models.addAll(createStatsModels(data.visibleRuns))
            models.addAll(createRecentRunsModels(data.visibleRuns, data.allUsers))
        } else {
            models.add(
                EmptyTextCellModel(
                    id = CellId.EMPTY_MESSAGE,
                    message = resourceProvider.getString(R.string.no_runs)
                )
            )
        }

        return models.toViewModels(intentProvider)
    }

    private fun createGroupTitleModels(
        title: String,
        isDriverRunning: Boolean,
        isAppInstalled: Boolean
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(
            LabeledTextCellModel(
                id = CellId.TEST_NAME,
                label = resourceProvider.getString(R.string.group),
                text = title,
                shape = CornersShape.TOP
            )
        )

        models.add(
            ButtonCellModel(
                id = CellId.RUN_TEST_BUTTON,
                text = resourceProvider.getString(R.string.run_upper),
                isButtonEnabled = (isDriverRunning && isAppInstalled),
                buttonColor = themeProvider.theme.colors.greenButton,
                shape = CornersShape.BOTTOM
            )
        )

        return models
    }

    private fun createTestTitleModels(
        flow: FlowEntry,
        isDriverRunning: Boolean,
        isAppInstalled: Boolean
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(
            LabeledTextCellModel(
                id = CellId.TEST_NAME,
                label = resourceProvider.getString(R.string.test),
                text = flow.name,
                shape = CornersShape.TOP
            )
        )

        models.add(
            ButtonCellModel(
                id = CellId.RUN_TEST_BUTTON,
                text = resourceProvider.getString(R.string.run_upper),
                isButtonEnabled = (isDriverRunning && isAppInstalled),
                buttonColor = themeProvider.theme.colors.greenButton,
                shape = CornersShape.BOTTOM
            )
        )

        return models
    }

    private fun createDriverStatusModels(isDriverRunning: Boolean): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val textColor = if (isDriverRunning) {
            themeProvider.theme.colors.testGreen
        } else {
            themeProvider.theme.colors.testRed
        }

        val cardColor = if (isDriverRunning) {
            themeProvider.theme.colors.greenCard
        } else {
            themeProvider.theme.colors.redCard
        }

        models.add(
            TextWithChipCellModel(
                id = CellId.DRIVER_STATUS,
                text = resourceProvider.getString(R.string.driver),
                chipText = if (isDriverRunning) {
                    resourceProvider.getString(R.string.running_upper)
                } else {
                    resourceProvider.getString(R.string.stopped_upper)
                },
                chipTextColor = textColor,
                chipColor = cardColor,
                shape = if (isDriverRunning) CornersShape.ALL else CornersShape.TOP
            )
        )

        if (!isDriverRunning) {
            models.add(
                ButtonCellModel(
                    id = CellId.DRIVER_BUTTON,
                    text = resourceProvider.getString(R.string.enable_driver_upper),
                    isButtonEnabled = true,
                    buttonColor = themeProvider.theme.colors.primaryButton,
                    shape = CornersShape.BOTTOM
                )
            )
        }

        return models
    }

    private fun createAppInfoModels(
        project: ProjectEntry,
        requiredAppVersion: AppVersion?,
        installedAppData: ExternalAppData?
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()
        val installedVersion = installedAppData?.appVersion
        val isInstalled = (
            (requiredAppVersion == null && installedVersion != null) ||
                (installedVersion != null && requiredAppVersion?.name == installedVersion.name)
            )

        models.add(
            LabeledTextCellModel(
                id = CellId.APPLICATION_NAME,
                label = resourceProvider.getString(R.string.application),
                text = project.name,
                shape = CornersShape.TOP
            )
        )

        models.add(
            LabeledTableCellModel(
                id = CellId.APPLICATION_VERSIONS,
                labels = listOf(
                    resourceProvider.getString(R.string.required_version),
                    resourceProvider.getString(R.string.installed_version)
                ),
                values = listOf(
                    requiredAppVersion?.name ?: resourceProvider.getString(R.string.any),
                    installedAppData?.appVersion?.name ?: DASH
                ),
                shape = if (isInstalled) CornersShape.BOTTOM else CornersShape.NONE
            )
        )

        if (!isInstalled) {
            models.add(
                ButtonCellModel(
                    id = CellId.APPLICATION_BUTTON,
                    isButtonEnabled = true,
                    buttonColor = themeProvider.theme.colors.greenButton,
                    text = resourceProvider.getString(R.string.install_app),
                    shape = CornersShape.BOTTOM
                )
            )
        }

        return models
    }

    private fun createStatsModels(runs: List<FlowRunEntry>): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val totalRuns = runs.size
        val passedRuns = runs.count { run -> run.isSuccess }
        val failedRuns = runs.count { run -> !run.isSuccess }
        val successRate = if (totalRuns != 0) {
            (passedRuns * 100 / totalRuns)
        } else {
            0
        }

        models.add(
            LabeledTableCellModel(
                id = CellId.STATISTICS_TOTAL_AND_RATE,
                labels = listOf(
                    resourceProvider.getString(R.string.total_runs),
                    resourceProvider.getString(R.string.success_rate)
                ),
                values = listOf(
                    totalRuns.toString(),
                    "$successRate%"
                ),
                shape = CornersShape.TOP
            )
        )

        models.add(
            LabeledTableCellModel(
                id = CellId.STATISTICS_PASSED_AND_FAILED,
                labels = listOf(
                    resourceProvider.getString(R.string.passed),
                    resourceProvider.getString(R.string.failed)
                ),
                values = listOf(
                    passedRuns.toString(),
                    failedRuns.toString()
                ),
                shape = CornersShape.BOTTOM
            )
        )

        return models
    }

    private fun createTestListModels(
        flows: List<FlowEntry>,
        runs: List<FlowRunEntry>
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        // TODO: show only last 10 tests
        models.add(
            HeaderCellModel(
                id = CellId.TESTS_HEADER,
                title = resourceProvider.getString(R.string.tests),
                iconText = null,
                icon = null
            )
        )

        val flowUidToRunsMap = runs.aggregateByFlowUid()
        val sortedFlows = flows.sortByRunsAndName(flowUidToRunsMap)

        for ((index, flow) in sortedFlows.withIndex()) {
            if (index > 0) {
                models.add(SpaceCellModel(height = SmallMargin))
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
                        ?: resourceProvider.getString(R.string.no_runs),
                    chipText = resourceProvider.getString(R.string.runs_with_number, flowRuns.size)
                )
            )
        }

        models.add(SpaceCellModel(ElementMargin))

        return models
    }

    private fun createRecentRunsModels(
        runs: List<FlowRunEntry>,
        users: List<UserEntry>
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val userMap = users
            .associateBy { user -> user.uid }

        val sortedRuns = runs.sortedByDescending { run -> run.finishedAt }
        val visibleRuns = sortedRuns.take(10)

        val headerIcon = if (sortedRuns.size > visibleRuns.size) {
            AppIcons.ArrowForward
        } else {
            null
        }

        models.add(
            HeaderCellModel(
                id = CellId.RECENT_RUNS_HEADER,
                title = resourceProvider.getString(R.string.recent_runs),
                iconText = null,
                icon = headerIcon
            )
        )

        for ((idx, run) in visibleRuns.withIndex()) {
            if (idx > 0) {
                models.add(SpaceCellModel(height = SmallMargin))
            }

            val userName = userMap[run.userUid]
                ?.name
                ?: StringUtils.EMPTY

            val icon = if (run.isSuccess) {
                AppIcons.CheckCircle
            } else {
                AppIcons.ErrorCircle
            }

            models.add(
                HistoryItemCellModel(
                    id = CellId.createRunCellId(run.uid),
                    icon = icon,
                    iconTint = if (run.isSuccess) {
                        IconTint.GREEN
                    } else {
                        IconTint.RED
                    },
                    title = run.finishedAt.formatRunTime(resourceProvider),
                    description = resourceProvider.getString(R.string.by_with_str, userName)
                )
            )
        }

        models.add(SpaceCellModel(height = ElementMargin))

        return models
    }

    private fun formatGroupPath(groups: List<GroupEntry>): String {
        return groups.joinToString(
            separator = SPACE + SLASH + SPACE,
            transform = { group -> group.name }
        )
    }

    private fun List<FlowEntry>.sortByRunsAndName(
        flowUidToRunsMap: Map<String, List<FlowRunEntry>>
    ): List<FlowEntry> {
        val sortedByRuns = this.mapNotNull { flow ->
            val runs = flowUidToRunsMap[flow.uid] ?: emptyList()
            if (runs.isNotEmpty()) {
                flow to runs.first()
            } else {
                null
            }
        }
            .sortedByDescending { (_, lastRun) -> lastRun.finishedAt }
            .map { (flow, _) -> flow }

        val sortedByName = filter { flow ->
            val runs = flowUidToRunsMap[flow.uid] ?: emptyList()
            runs.isEmpty()
        }
            .sortedBy { flow -> flow.name }

        return sortedByRuns + sortedByName
    }

    private fun newElementSpaceModel(): SpaceCellModel = SpaceCellModel(height = ElementMargin)

    object CellId {
        const val EMPTY_MESSAGE = "empty-message"
        const val DRIVER_STATUS = "driver-status"
        const val DRIVER_BUTTON = "driver-button"
        const val APPLICATION_NAME = "app-name"
        const val APPLICATION_VERSIONS = "app-versions"
        const val APPLICATION_BUTTON = "app-button"
        const val TEST_NAME = "test-name"
        const val RUN_TEST_BUTTON = "run-test-button"
        const val STATISTICS_TOTAL_AND_RATE = "stats-success-rate"
        const val STATISTICS_PASSED_AND_FAILED = "stats-passed-failed"
        const val RECENT_RUNS_HEADER = "recent-runs-header"
        const val TESTS_HEADER = "tests-header"

        private const val RUN_PREFIX = "run_"

        fun createRunCellId(runUid: String): String = RUN_PREFIX + runUid

        fun extractRunUid(cellId: String): String? {
            return if (cellId.contains(RUN_PREFIX)) {
                cellId.removePrefix(RUN_PREFIX)
            } else {
                null
            }
        }
    }
}