package com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells

import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.HeaderCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.model.TestRunScreenData
import com.github.aivanovski.testswithme.utils.StringUtils

class TestRunCellFactory {

    fun createCellViewModels(
        data: TestRunScreenData,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createModels(data).map { model ->
            when (model) {
                is SpaceCellModel -> SpaceCellViewModel(model)
                is HeaderCellModel -> HeaderCellViewModel(model, intentProvider)
                else -> createCoreCellViewModel(model, intentProvider)
            }
        }
    }

    private fun createModels(data: TestRunScreenData): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(SpaceCellModel(ElementMargin))

        models.add(
            HeaderCellModel(
                id = "header", // TODO: fix
                title = data.flow.name,
                iconText = null,
                icon = null
            )
        )

        val lines = data.flowContent
            .split(StringUtils.NEW_LINE)
            .filter { line -> line.isNotEmpty() }

        for ((index, line) in lines.withIndex()) {
            models.add(
                TextCellModel(
                    id = "line_$index",
                    text = line
                )
            )
        }

        return models
    }
}