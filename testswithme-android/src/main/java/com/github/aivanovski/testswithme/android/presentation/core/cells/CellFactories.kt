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
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.LabeledTextWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.MenuCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ShapedSpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextButtonCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipRowCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TitleWithIconCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TwoLineTextWithChipCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedChipRowCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.ButtonCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.DividerCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.EmptyTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.HeaderCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.HeaderWithDescriptionCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.IconChipRowCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.IconTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.IconThreeTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.LabeledTableCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.LabeledTextWithIconCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.MenuCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.ShapedSpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.SpaceCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TextButtonCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TextChipRowCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TextWithChipCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TitleWithIconCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.TwoLineTextWithChipCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.UnshapedChipRowCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.UnshapedTextCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.ButtonCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.DividerCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.EmptyTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.HeaderCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.HeaderWithDescriptionCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.IconThreeTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.LabeledTableCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.LabeledTextWithIconCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.MenuCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.ShapedSpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextButtonCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TextWithChipCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TitleWithIconCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.TwoLineWithChipCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.UnshapedChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.UnshapedTextCellViewModel

@Composable
fun CreateCoreCell(viewModel: CellViewModel) {
    when (viewModel) {
        is ButtonCellViewModel -> ButtonCell(viewModel)
        is TextButtonCellViewModel -> TextButtonCell(viewModel)
        is HeaderCellViewModel -> HeaderCell(viewModel)
        is HeaderWithDescriptionCellViewModel -> HeaderWithDescriptionCell(viewModel)
        is IconThreeTextCellViewModel -> IconThreeTextCell(viewModel)
        is IconTextCellViewModel -> IconTextCell(viewModel)
        is LabeledTableCellViewModel -> LabeledTableCell(viewModel)
        is LabeledTextWithIconCellViewModel -> LabeledTextWithIconCell(viewModel)
        is SpaceCellViewModel -> SpaceCell(viewModel)
        is ShapedSpaceCellViewModel -> ShapedSpaceCell(viewModel)
        is TextWithChipCellViewModel -> TextWithChipCell(viewModel)
        is IconChipRowCellViewModel -> IconChipRowCell(viewModel)
        is TextChipRowCellViewModel -> TextChipRowCell(viewModel)
        is TextCellViewModel -> TextCell(viewModel)
        is EmptyTextCellViewModel -> EmptyTextCell(viewModel)
        is DividerCellViewModel -> DividerCell(viewModel)
        is MenuCellViewModel -> MenuCell(viewModel)
        is TitleWithIconCellViewModel -> TitleWithIconCell(viewModel)
        is UnshapedTextCellViewModel -> UnshapedTextCell(viewModel)
        is TwoLineWithChipCellViewModel -> TwoLineTextWithChipCell(viewModel)
        is UnshapedChipRowCellViewModel -> UnshapedChipRowCell(viewModel)
        else -> throw UnsupportedCellViewModelException(viewModel)
    }
}

fun createCoreCellViewModel(
    model: BaseCellModel,
    intentProvider: CellIntentProvider
): BaseCellViewModel {
    return when (model) {
        is ButtonCellModel -> ButtonCellViewModel(model, intentProvider)
        is TextButtonCellModel -> TextButtonCellViewModel(model, intentProvider)
        is HeaderCellModel -> HeaderCellViewModel(model, intentProvider)
        is HeaderWithDescriptionCellModel -> HeaderWithDescriptionCellViewModel(model)
        is IconThreeTextCellModel -> IconThreeTextCellViewModel(model, intentProvider)
        is IconTextCellModel -> IconTextCellViewModel(model, intentProvider)
        is LabeledTextWithIconCellModel -> LabeledTextWithIconCellViewModel(model, intentProvider)
        is LabeledTableCellModel -> LabeledTableCellViewModel(model, intentProvider)
        is SpaceCellModel -> SpaceCellViewModel(model)
        is ShapedSpaceCellModel -> ShapedSpaceCellViewModel(model)
        is TextWithChipCellModel -> TextWithChipCellViewModel(model)
        is IconChipRowCellModel -> IconChipRowCellViewModel(model)
        is TextChipRowCellModel -> TextChipRowCellViewModel(model, intentProvider)
        is TextCellModel -> TextCellViewModel(model)
        is EmptyTextCellModel -> EmptyTextCellViewModel(model)
        is DividerCellModel -> DividerCellViewModel(model)
        is MenuCellModel -> MenuCellViewModel(model, intentProvider)
        is TitleWithIconCellModel -> TitleWithIconCellViewModel(model, intentProvider)
        is UnshapedTextCellModel -> UnshapedTextCellViewModel(model)
        is TwoLineTextWithChipCellModel -> TwoLineWithChipCellViewModel(model, intentProvider)
        is UnshapedChipRowCellModel -> UnshapedChipRowCellViewModel(model, intentProvider)
        else -> throw UnsupportedCellModelException(model)
    }
}