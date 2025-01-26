package com.github.aivanovski.testswithme.android.presentation.screens.textViewer.cells

import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedTextCellModel
import com.github.aivanovski.testswithme.extensions.splitIntoLines

class TextViewerCellFactory {

    fun createCellViewModels(
        content: String,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createModels(content).map { model ->
            createCoreCellViewModel(model, intentProvider)
        }
    }

    private fun createModels(content: String): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val lines = content.splitIntoLines()
        for ((lineIdx, line) in lines.withIndex()) {
            models.add(
                UnshapedTextCellModel(
                    id = "line_$lineIdx",
                    text = line,
                    textSize = TextSize.BODY_MEDIUM
                )
            )
        }

        return models
    }
}