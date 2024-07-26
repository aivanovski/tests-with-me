package com.github.aivanovski.testwithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.ShapedSpaceCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.ShapedSpaceCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.GroupMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.toComposeShape

@Composable
fun ShapedSpaceCell(viewModel: ShapedSpaceCellViewModel) {
    val model = viewModel.model

    Card(
        shape = model.shape.toComposeShape(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.cardOnSecondaryBackground
        ),
        modifier = Modifier
            .padding(
                horizontal = ElementMargin
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = model.height)
        )
    }
}

@Composable
@Preview
fun ShapedSpaceCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        ShapedSpaceCell(newShapedSpaceCellViewModel(height = GroupMargin))
    }
}

fun newShapedSpaceCellViewModel(
    height: Dp,
    shape: CornersShape = CornersShape.ALL
) = ShapedSpaceCellViewModel(
    model = ShapedSpaceCellModel(
        height = height,
        shape = shape
    )
)