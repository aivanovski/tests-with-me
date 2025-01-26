package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedTextCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.UnshapedTextCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ElementSpace
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.longText
import com.github.aivanovski.testswithme.android.presentation.core.compose.shortText
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.toTextStyle

@Composable
fun UnshapedTextCell(viewModel: UnshapedTextCellViewModel) {
    val model = viewModel.model

    Text(
        text = model.text,
        color = AppTheme.theme.colors.primaryText,
        style = model.textSize.toTextStyle(),
        modifier = Modifier
            .padding(
                horizontal = ElementMargin
            )
    )
}

@Composable
@Preview
fun UnshapedTextCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            UnshapedTextCell(newUnshapedTextCell())
            ElementSpace()
            UnshapedTextCell(newUnshapedTextCell(text = longText()))
        }
    }
}

@Composable
fun newUnshapedTextCell(text: String = shortText()): UnshapedTextCellViewModel =
    UnshapedTextCellViewModel(
        model = UnshapedTextCellModel(
            id = "id",
            text = text,
            textSize = TextSize.BODY_MEDIUM
        )
    )