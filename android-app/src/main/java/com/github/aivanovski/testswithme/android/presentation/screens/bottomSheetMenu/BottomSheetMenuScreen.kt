package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.cells.CreateCoreCell
import com.github.aivanovski.testswithme.android.presentation.core.cells.ui.newMenuCell
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.events.SingleEventEffect
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.DialogCardCornerSize
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetUiEvent

@Composable
fun BottomSheetMenuScreen(
    viewModel: BottomSheetMenuViewModel,
    eventCollector: (event: BottomSheetUiEvent) -> Unit
) {
    val state by viewModel.state.collectAsState()

    BottomSheetMenuScreen(
        state = state
    )

    SingleEventEffect(
        eventFlow = viewModel.events,
        collector = eventCollector
    )
}

@Composable
private fun BottomSheetMenuScreen(state: BottomSheetMenuState) {
    Card(
        shape = RoundedCornerShape(DialogCardCornerSize, DialogCardCornerSize, 0.dp, 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.background
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = ElementMargin
                )
        ) {
            items(state.viewModels) { cellViewModel ->
                CreateCoreCell(cellViewModel)
            }
        }
    }
}

@Preview
@Composable
fun BottomSheetMenuScreenPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = Color.Transparent
    ) {
        BottomSheetMenuScreen(state = newScreenState())
    }
}

@Composable
private fun newScreenState() =
    BottomSheetMenuState(
        viewModels = listOf(
            newMenuCell(AppIcons.Login, stringResource(R.string.log_in_join)),
            newMenuCell(AppIcons.Settings, stringResource(R.string.settings))
        )
    )