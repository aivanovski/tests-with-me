package com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells

import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.entity.exception.UnsupportedCellModelException
import com.github.aivanovski.testwithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.HeaderCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.models.TextCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.cells.viewModel.TextCellViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.testRun.model.TestRunScreenData
import com.github.aivanovski.testwithme.utils.StringUtils

class TestRunCellFactory(
    private val resourceProvider: ResourceProvider
) {

    fun createCellViewModels(
        data: TestRunScreenData,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createModels(data).map { model ->
            when (model) {
                is SpaceCellModel -> SpaceCellViewModel(model)
                is TextCellModel -> TextCellViewModel(model)
                is HeaderCellModel -> HeaderCellViewModel(model, intentProvider)
                else -> throw UnsupportedCellModelException(model)
            }
        }
    }

    private fun createModels(
        data: TestRunScreenData
    ): List<BaseCellModel> {
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