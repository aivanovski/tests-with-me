package com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.models.TextCellModel
import com.github.aivanovski.testswithme.android.presentation.screens.testRun.cells.viewModel.TextCellViewModel

@Composable
fun TextCell(viewModel: TextCellViewModel) {
    Text(
        text = viewModel.model.text,
        color = AppTheme.theme.colors.primaryText,
        style = AppTheme.theme.typography.bodyLarge,
        modifier = Modifier
            .padding(
                horizontal = ElementMargin
            )
    )
}

@Composable
@Preview
fun TextCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        Column {
            TextCell(newShortTextCell())

            Spacer(Modifier.height(ElementMargin))

            TextCell(newLongTextCell())
        }
    }
}

fun newShortTextCell(): TextCellViewModel {
    return TextCellViewModel(
        model = TextCellModel(
            id = "id",
            text = "- launch: com.fdroid.fdroid"
        )
    )
}

fun newLongTextCell(): TextCellViewModel {
    return TextCellViewModel(
        model = TextCellModel(
            id = "id",
            text = "- sendBroadcast: com.ivanovsky.passnotes/" +
                "com.ivanovsky.passnotes.domain.test.TestDataBroadcastReceiver"
        )
    )
}