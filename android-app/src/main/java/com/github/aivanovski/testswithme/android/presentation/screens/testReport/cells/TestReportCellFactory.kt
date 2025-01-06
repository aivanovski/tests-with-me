package com.github.aivanovski.testswithme.android.presentation.screens.testReport.cells

import com.github.aivanovski.testswithme.android.entity.FlowRunWithReport
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextCellModel
import com.github.aivanovski.testswithme.extensions.splitIntoLines

class TestReportCellFactory {

    fun createCellViewModels(
        flowRunWithReport: FlowRunWithReport,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createModels(flowRunWithReport).map { model ->
            createCoreCellViewModel(model, intentProvider)
        }
    }

    private fun createModels(flowRunWithReport: FlowRunWithReport): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        val lines = flowRunWithReport.report.splitIntoLines()
        for ((lineIdx, line) in lines.withIndex()) {
            models.add(
                TextCellModel(
                    id = "line_$lineIdx",
                    text = line
                )
            )
        }

        return models
    }
}