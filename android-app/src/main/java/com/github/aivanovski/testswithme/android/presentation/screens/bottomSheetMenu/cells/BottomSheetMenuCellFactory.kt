package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.cells

import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.CellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.createCoreCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.MenuCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetItem

class BottomSheetMenuCellFactory {

    fun createCellViewModels(
        items: List<BottomSheetItem>,
        intentProvider: CellIntentProvider
    ): List<CellViewModel> {
        return createModels(items).map { model ->
            createCoreCellViewModel(model, intentProvider)
        }
    }

    private fun createModels(items: List<BottomSheetItem>): List<BaseCellModel> {
        return items.mapIndexed { index, item ->
            MenuCellModel(
                id = index.toString(),
                icon = item.icon.value,
                title = item.title
            )
        }
    }
}