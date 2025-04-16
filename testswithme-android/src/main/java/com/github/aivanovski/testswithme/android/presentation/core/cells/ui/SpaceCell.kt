package com.github.aivanovski.testswithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme

@Composable
fun SpaceCell(viewModel: SpaceCellViewModel) {
    Box(
        modifier = Modifier
            .height(height = viewModel.model.height)
    )
}

@Composable
@Preview
fun SpaceCellLightPreview() {
    ThemedPreview(theme = LightTheme) {
        Column {
            SpaceCell(newSpaceCell(ElementMargin))
        }
    }
}

fun newSpaceCell(height: Dp): SpaceCellViewModel {
    return SpaceCellViewModel(
        model = SpaceCellModel(
            id = "space",
            height = height
        )
    )
}