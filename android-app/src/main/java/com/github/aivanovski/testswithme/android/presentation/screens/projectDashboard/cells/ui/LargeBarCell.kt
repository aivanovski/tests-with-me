package com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.toComposeShape
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.model.LargeBarCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.cells.viewModel.LargeBarCellViewModel

@Composable
fun LargeBarCell(viewModel: LargeBarCellViewModel) {
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
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CircularProgressIndicator(
                progress = model.progress,
                color = AppTheme.theme.colors.testGreen,
                trackColor = Color(0xFF_EEEEEE),
                strokeWidth = 8.dp,
                modifier = Modifier
                    .size(156.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Text(
                    text = model.title,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.headlineLarge
                )

                Text(
                    text = model.subtitle,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
@Preview
fun LargeBarCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        LargeBarCell(newLargeBarCellViewModel())
    }
}

fun newLargeBarCellViewModel(): LargeBarCellViewModel =
    LargeBarCellViewModel(
        model = LargeBarCellModel(
            id = "id",
            progress = 0.72f,
            title = "72%",
            subtitle = "in progress",
            shape = CornersShape.NONE
        )
    )