package com.github.aivanovski.testswithme.android.presentation.screens.flow.cells

import androidx.compose.ui.graphics.Color
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.AppVersion
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.FlowRunEntry
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.db.StepEntry
import com.github.aivanovski.testswithme.android.entity.db.UserEntry
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.factory.SpaceCellFactory
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.EmptyTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextButtonCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TwoLineTextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.model.HistoryItemCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.cells.viewModel.HistoryItemCellViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.ExternalAppData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowData
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowSelection
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.model.FlowCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.groups.cells.viewModel.FlowCellViewModel
import com.github.aivanovski.testswithme.android.utils.aggregateByFlowUid
import com.github.aivanovski.testswithme.android.utils.findParentGroups
import com.github.aivanovski.testswithme.android.utils.formatRunTime
import com.github.aivanovski.testswithme.flow.formatter.FlowStepFormatter
import com.github.aivanovski.testswithme.flow.formatter.FlowStepFormatter.Format
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.utils.StringUtils.DASH
import com.github.aivanovski.testswithme.utils.StringUtils.SLASH
import com.github.aivanovski.testswithme.utils.StringUtils.SPACE

class FlowCellFactory(
    private val resourceProvider: ResourceProvider,
    private val themeProvider: ThemeProvider
) {

    private val stepFormatter = FlowStepFormatter()

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

        val spaceCellFactory = SpaceCellFactory(CellId.SECTION_SPACE_PREFIX)
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        models.addAll(createDriverStatusModels(isDriverRunning))
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        if (data.project != null) {
            models.addAll(
                createApplicationInfoModels(
                    project = data.project,
                    requiredAppVersion = null,
                    installedAppData = installedAppData
                )
            )
            models.add(spaceCellFactory.newSpaceCell(ElementMargin))
        }

        val parents = findParentGroups(group, data.allGroups)

        models.addAll(
            createGroupTitleModels(
                title = formatGroupPath(parents.plus(group)),
                isDriverRunning = isDriverRunning,
                isAppInstalled = isAppInstalled
            )
        )
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

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
        selection: FlowSelection,
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

        val spaceCellFactory = SpaceCellFactory(CellId.SECTION_SPACE_PREFIX)

        models.add(spaceCellFactory.newSpaceCell(ElementMargin))
        models.addAll(createDriverStatusModels(isDriverRunning))
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        if (data.project != null) {
            models.addAll(
                createApplicationInfoModels(
                    project = data.project,
                    requiredAppVersion = requiredAppVersion,
                    installedAppData = installedAppData
                )
            )
            models.add(spaceCellFactory.newSpaceCell(ElementMargin))
        }

        val title = when (selection) {
            is FlowSelection.Remained -> resourceProvider.getString(R.string.remained_tests)
            is FlowSelection.Passed -> resourceProvider.getString(R.string.passed_tests)
            is FlowSelection.Failed -> resourceProvider.getString(R.string.failed_tests)
        }

        models.addAll(
            createGroupTitleModels(
                title = title,
                isDriverRunning = isDriverRunning,
                isAppInstalled = isAppInstalled
            )
        )

        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

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

    fun createLocalFlowCellViewModels(
        data: FlowData,
        isDriverRunning: Boolean,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        val models = mutableListOf<BaseCellModel>()
        val flow = data.visibleFlows.firstOrNull() ?: return emptyList()

        val spaceCellFactory = SpaceCellFactory(CellId.SECTION_SPACE_PREFIX)

        models.add(spaceCellFactory.newSpaceCell(ElementMargin))
        models.addAll(createDriverStatusModels(isDriverRunning))
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        models.addAll(
            createTestTitleModels(
                flow = flow,
                isDriverRunning = isDriverRunning,
                isAppInstalled = true
            )
        )
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        models.addAll(
            createStepModels(
                steps = data.steps.getOrNull() ?: emptyList()
            )
        )
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        if (data.visibleJobs.isNotEmpty()) {
            models.addAll(
                createRecentRunsModels(
                    jobs = data.visibleJobs,
                    user = data.user
                )
            )
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

    private fun createStepModels(steps: List<StepEntry>): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val visibleSteps = steps.take(5)

        if (steps.isNotEmpty()) {
            models.add(
                HeaderCellModel(
                    id = CellId.STEPS_HEADER,
                    title = resourceProvider.getString(R.string.steps),
                    iconText = resourceProvider.getString(R.string.view),
                    icon = AppIcons.ArrowForward
                )
            )

            val stepsLeft = steps.size - visibleSteps.size
            for ((stepIndex, step) in visibleSteps.withIndex()) {
                val shape = when {
                    visibleSteps.size == 1 -> CornersShape.ALL
                    stepIndex == 0 && visibleSteps.size > 1 -> CornersShape.TOP
                    stepIndex == visibleSteps.lastIndex && stepsLeft == 0 -> CornersShape.BOTTOM
                    else -> CornersShape.NONE
                }

                val descriptionLines = stepFormatter.format(step.command, Format.SHORT)
                    .split(StringUtils.NEW_LINE)

                val title = descriptionLines.first()
                val description = descriptionLines.takeLast(descriptionLines.size - 1)
                    .filter { line -> line.isNotEmpty() }
                    .joinToString(separator = StringUtils.NEW_LINE)

                models.add(
                    TwoLineTextWithChipCellModel(
                        id = CellId.createStepCellId(step.uid),
                        title = title,
                        description = description,
                        shape = shape,
                        chipText = StringUtils.EMPTY,
                        chipTextColor = Color.Unspecified,
                        chipColor = Color.Unspecified
                    )
                )
            }

            if (stepsLeft > 0) {
                models.add(
                    TextButtonCellModel(
                        id = CellId.MORE_STEPS_BUTTON,
                        text = resourceProvider.getString(R.string.show_more_with_str, stepsLeft),
                        shape = CornersShape.BOTTOM
                    )
                )
            }
        } else {
            models.add(
                EmptyTextCellModel(
                    id = CellId.EMPTY_MESSAGE,
                    message = resourceProvider.getString(R.string.no_steps)
                )
            )
        }

        return models
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

        val spaceCellFactory = SpaceCellFactory(CellId.GROUP_SPACE_PREFIX)

        models.add(spaceCellFactory.newSpaceCell(ElementMargin))
        models.addAll(createDriverStatusModels(isDriverRunning))
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        if (data.project != null) {
            models.addAll(
                createApplicationInfoModels(
                    project = data.project,
                    requiredAppVersion = requiredAppVersion,
                    installedAppData = installedAppData
                )
            )
            models.add(spaceCellFactory.newSpaceCell(ElementMargin))
        }

        models.addAll(
            createTestTitleModels(
                flow = flow,
                isDriverRunning = isDriverRunning,
                isAppInstalled = isAppInstalled
            )
        )
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        val steps = data.steps.getOrNull() ?: emptyList()

        if (data.visibleRuns.isNotEmpty()) {
            models.addAll(createStatsModels(data.visibleRuns))

            if (steps.isNotEmpty()) {
                models.add(spaceCellFactory.newSpaceCell(ElementMargin))
            }
        }

        models.addAll(createStepModels(steps))
        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        if (data.visibleRuns.isNotEmpty()) {
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
            LabeledTextWithIconCellModel(
                id = CellId.TEST_NAME,
                label = resourceProvider.getString(R.string.group),
                text = title,
                icon = null,
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
            LabeledTextWithIconCellModel(
                id = CellId.TEST_NAME,
                label = resourceProvider.getString(R.string.test),
                text = flow.name,
                icon = null,
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
                textSize = TextSize.BODY_LARGE,
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

    private fun createApplicationInfoModels(
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
                id = CellId.APPLICATION_VERSIONS,
                labels = listOf(
                    resourceProvider.getString(R.string.required_version),
                    resourceProvider.getString(R.string.installed_version)
                ),
                values = listOf(
                    requiredAppVersion?.name ?: resourceProvider.getString(R.string.any),
                    installedAppData?.appVersion?.name ?: DASH
                ),
                isClickable = false,
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
                isClickable = false,
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
                isClickable = false,
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
        val spaceCellFactory = SpaceCellFactory(CellId.TEST_SPACE_PREFIX)

        for ((index, flow) in sortedFlows.withIndex()) {
            if (index > 0) {
                models.add(spaceCellFactory.newSpaceCell(SmallMargin))
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
                    id = CellId.createFlowCellId(flow.uid),
                    icon = icon,
                    iconTint = iconTint,
                    title = flow.name,
                    description = lastRun?.finishedAt?.formatRunTime(resourceProvider)
                        ?: resourceProvider.getString(R.string.no_runs),
                    chipText = resourceProvider.getString(R.string.runs_with_number, flowRuns.size)
                )
            )
        }

        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

        return models
    }

    private fun createRecentRunsModels(
        jobs: List<JobEntry>,
        user: UserEntry
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val sortedJobs = jobs
            .filter { job -> job.finishedTimestamp != null }
            .sortedByDescending { job -> job.finishedTimestamp }
        val visibleJobs = sortedJobs.take(10)

        val headerIcon = if (sortedJobs.size > visibleJobs.size) {
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

        val spaceCellFactory = SpaceCellFactory(CellId.RUN_SPACE_PREFIX)

        for ((idx, job) in visibleJobs.withIndex()) {
            if (idx > 0) {
                models.add(spaceCellFactory.newSpaceCell(SmallMargin))
            }

            val isSuccessfullyFinished = (job.executionResult == ExecutionResult.SUCCESS)

            val icon = when (job.executionResult) {
                ExecutionResult.SUCCESS -> AppIcons.CheckCircle
                ExecutionResult.FAILED -> AppIcons.ErrorCircle
                ExecutionResult.NONE -> AppIcons.CheckCircle
            }

            val time = job.finishedTimestamp?.formatRunTime(resourceProvider)
                ?: ""

            models.add(
                HistoryItemCellModel(
                    id = CellId.createJobCellId(job.uid),
                    icon = icon,
                    iconTint = if (isSuccessfullyFinished) IconTint.GREEN else IconTint.RED,
                    title = time,
                    description = user.name
                )
            )
        }

        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

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

        val spaceCellFactory = SpaceCellFactory(CellId.RUN_SPACE_PREFIX)

        for ((idx, run) in visibleRuns.withIndex()) {
            if (idx > 0) {
                models.add(spaceCellFactory.newSpaceCell(SmallMargin))
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

        models.add(spaceCellFactory.newSpaceCell(ElementMargin))

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
        const val STEPS_HEADER = "steps-header"
        const val MORE_STEPS_BUTTON = "more-steps-button"

        const val GROUP_SPACE_PREFIX = "group_space_"
        const val TEST_SPACE_PREFIX = "test_space_"
        const val RUN_SPACE_PREFIX = "run_space_"
        const val SECTION_SPACE_PREFIX = "section_space_"

        private const val RUN_PREFIX = "run_"
        private const val JOB_PREFIX = "job_"
        private const val STEP_PREFIX = "step_"
        private const val FLOW_PREFIX = "flow_"

        fun createRunCellId(runUid: String): String = RUN_PREFIX + runUid

        fun createJobCellId(jobUid: String): String = JOB_PREFIX + jobUid

        fun createStepCellId(stepUid: String): String = STEP_PREFIX + stepUid

        fun createFlowCellId(flowUid: String): String = FLOW_PREFIX + flowUid

        fun extractRunUid(cellId: String): String? =
            if (cellId.startsWith(RUN_PREFIX)) cellId.removePrefix(RUN_PREFIX) else null

        fun extractJobUid(cellId: String): String? =
            if (cellId.startsWith(JOB_PREFIX)) cellId.removePrefix(JOB_PREFIX) else null

        fun extractFlowUid(cellId: String): String? =
            if (cellId.startsWith(FLOW_PREFIX)) cellId.removePrefix(FLOW_PREFIX) else null

        fun hasRunUid(cellId: String): Boolean = cellId.startsWith(RUN_PREFIX)

        fun hasJobUid(cellId: String): Boolean = cellId.startsWith(JOB_PREFIX)

        fun hasFlowUid(cellId: String): Boolean = cellId.startsWith(FLOW_PREFIX)
    }
}