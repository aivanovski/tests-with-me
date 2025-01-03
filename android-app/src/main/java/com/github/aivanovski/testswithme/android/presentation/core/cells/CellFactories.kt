package com.github.aivanovski.testswithme.android.presentation.core.cells

import androidx.compose.runtime.Composable
import com.github.aivanovski.testswithme.android.entity.exception.UnsupportedCellModelException
import com.github.aivanovski.testswithme.android.entity.exception.UnsupportedCellViewModelException
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ButtonCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.DividerCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.EmptyTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.HeaderWithDescriptionCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconChipRowCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.IconThreeTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTableCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.MenuCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ShapedSpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipRowCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.ButtonCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.DividerCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.EmptyTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.HeaderCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.HeaderWithDescriptionCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.IconChipRowCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.IconTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.IconThreeTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.LabeledTableCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.LabeledTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.MenuCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.ShapedSpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.SpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TextChipRowCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TextWithChipCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TitleCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TitleWithIconCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.ButtonCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.DividerCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.EmptyTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.HeaderCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.HeaderWithDescriptionCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconThreeTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.LabeledTableCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.LabeledTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.MenuCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.ShapedSpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextWithChipCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TitleCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TitleWithIconCellViewModel

@Composable
fun CreateCoreCell(viewModel: CellViewModel) {
    when (viewModel) {
        is ButtonCellViewModel -> ButtonCell(viewModel)
        is HeaderCellViewModel -> HeaderCell(viewModel)
        is HeaderWithDescriptionCellViewModel -> HeaderWithDescriptionCell(viewModel)
        is IconThreeTextCellViewModel -> IconThreeTextCell(viewModel)
        is IconTextCellViewModel -> IconTextCell(viewModel)
        is LabeledTableCellViewModel -> LabeledTableCell(viewModel)
        is LabeledTextCellViewModel -> LabeledTextCell(viewModel)
        is SpaceCellViewModel -> SpaceCell(viewModel)
        is ShapedSpaceCellViewModel -> ShapedSpaceCell(viewModel)
        is TextWithChipCellViewModel -> TextWithChipCell(viewModel)
        is IconChipRowCellViewModel -> IconChipRowCell(viewModel)
        is TextChipRowCellViewModel -> TextChipRowCell(viewModel)
        is TitleCellViewModel -> TitleCell(viewModel)
        is EmptyTextCellViewModel -> EmptyTextCell(viewModel)
        is DividerCellViewModel -> DividerCell(viewModel)
        is MenuCellViewModel -> MenuCell(viewModel)
        is TitleWithIconCellViewModel -> TitleWithIconCell(viewModel)
        else -> throw UnsupportedCellViewModelException(viewModel)
    }
}

fun createCoreCellViewModel(
    model: BaseCellModel,
    intentProvider: CellIntentProvider
): BaseCellViewModel {
    return when (model) {
        is ButtonCellModel -> ButtonCellViewModel(model, intentProvider)
        is HeaderCellModel -> HeaderCellViewModel(model, intentProvider)
        is HeaderWithDescriptionCellModel -> HeaderWithDescriptionCellViewModel(model)
        is IconThreeTextCellModel -> IconThreeTextCellViewModel(model, intentProvider)
        is IconTextCellModel -> IconTextCellViewModel(model, intentProvider)
        is LabeledTextCellModel -> LabeledTextCellViewModel(model)
        is LabeledTableCellModel -> LabeledTableCellViewModel(model)
        is SpaceCellModel -> SpaceCellViewModel(model)
        is ShapedSpaceCellModel -> ShapedSpaceCellViewModel(model)
        is TextWithChipCellModel -> TextWithChipCellViewModel(model)
        is IconChipRowCellModel -> IconChipRowCellViewModel(model)
        is TextChipRowCellModel -> TextChipRowCellViewModel(model, intentProvider)
        is TitleCellModel -> TitleCellViewModel(model)
        is EmptyTextCellModel -> EmptyTextCellViewModel(model)
        is DividerCellModel -> DividerCellViewModel(model)
        is MenuCellModel -> MenuCellViewModel(model, intentProvider)
        is TitleWithIconCellModel -> TitleWithIconCellViewModel(model, intentProvider)
        else -> throw UnsupportedCellModelException(model)
    }
}