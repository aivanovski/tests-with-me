package com.github.aivanovski.testswithme.android.presentation.screens.testRuns.cells

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.exception.UnsupportedCellModelException
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconThreeTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTint
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconThreeTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.model.TestRunsData
import com.github.aivanovski.testswithme.android.utils.formatRunTime
import com.github.aivanovski.testswithme.utils.StringUtils

class TestRunsCellFactory(
    private val resourceProvider: ResourceProvider
) {

    fun createCellViewModels(
        data: TestRunsData,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createModels(data).map { model ->
            when (model) {
                is IconThreeTextCellModel -> IconThreeTextCellViewModel(model, intentProvider)
                is SpaceCellModel -> SpaceCellViewModel(model)
                else -> throw UnsupportedCellModelException(model)
            }
        }
    }

    private fun createModels(data: TestRunsData): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(SpaceCellModel(ElementMargin))

        val allFlowsMap = data.allFlows
            .associateBy { flow -> flow.entry.uid }

        val allProjectsMap = data.allProjects
            .associateBy { project -> project.uid }

        val sortedJobHistory = data.jobHistory
            .sortedByDescending { job -> job.finishedTimestamp }

        for ((index, job) in sortedJobHistory.withIndex()) {
            val flow = allFlowsMap[job.flowUid] ?: continue

            if (index > 0) {
                models.add(SpaceCellModel(SmallMargin))
            }

            val icon = when (job.executionResult) {
                ExecutionResult.SUCCESS -> AppIcons.CheckCircle
                ExecutionResult.FAILED -> AppIcons.ErrorCircle
                ExecutionResult.NONE -> AppIcons.CheckCircle
            }

            val iconTint = when (job.executionResult) {
                ExecutionResult.SUCCESS -> IconTint.GREEN
                ExecutionResult.FAILED -> IconTint.RED
                ExecutionResult.NONE -> IconTint.PRIMARY_ICON
            }

            val description = if (flow.entry.sourceType == SourceType.REMOTE) {
                allProjectsMap[flow.entry.projectUid]?.name ?: StringUtils.EMPTY
            } else {
                resourceProvider.getString(R.string.local_file)
            }

            val time = job.finishedTimestamp?.formatRunTime(resourceProvider)
                ?: StringUtils.EMPTY

            models.add(
                IconThreeTextCellModel(
                    id = job.uid,
                    title = flow.entry.name,
                    description = description,
                    secondaryDescription = time,
                    icon = icon,
                    iconTint = iconTint
                )
            )
        }

        models.add(SpaceCellModel(ElementMargin))

        return models
    }
}