package com.github.aivanovski.testwithme.android.presentation.core.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.ButtonCellIntent
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.ButtonCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.ButtonCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.ElementSpace
import com.github.aivanovski.testwithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.toComposeShape

@Composable
fun ButtonCell(viewModel: ButtonCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(ButtonCellIntent.OnClick(model.id))
    }

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
        Button(
            onClick = onClick,
            enabled = model.isButtonEnabled,
            modifier = Modifier
                .padding(
                    start = ElementMargin,
                    end = ElementMargin,
                    bottom = SmallMargin
                )
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = model.buttonColor
            )
        ) {
            Text(
                text = model.text
            )
        }
    }
}

@Composable
@Preview
fun ButtonCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            ButtonCell(newPrimaryButtonCell())
            ElementSpace()
            ButtonCell(newPrimaryDisabledButtonCell())
            ElementSpace()
            ButtonCell(newGreenButtonCell())
        }
    }
}

@Composable
fun newPrimaryButtonCell(
    shape: CornersShape = CornersShape.ALL
) = ButtonCellViewModel(
    model = ButtonCellModel(
        id = "id",
        text = "RUN",
        isButtonEnabled = true,
        buttonColor = LightTheme.colors.primaryButton,
        shape = shape
    ),
    intentProvider = PreviewIntentProvider
)

@Composable
fun newPrimaryDisabledButtonCell(
    shape: CornersShape = CornersShape.ALL
) = ButtonCellViewModel(
    model = ButtonCellModel(
        id = "id",
        text = "RUN",
        isButtonEnabled = false,
        buttonColor = LightTheme.colors.primaryButton,
        shape = shape
    ),
    intentProvider = PreviewIntentProvider
)

@Composable
fun newGreenButtonCell(
    shape: CornersShape = CornersShape.ALL
) = ButtonCellViewModel(
    model = ButtonCellModel(
        id = "id",
        text = "RUN",
        isButtonEnabled = true,
        buttonColor = LightTheme.colors.greenButton,
        shape = shape
    ),
    intentProvider = PreviewIntentProvider
)