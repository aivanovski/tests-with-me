package com.github.aivanovski.testswithme.android.presentation.core.cells.factory

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.DividerCellModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import java.util.concurrent.atomic.AtomicInteger

class DividerCellFactory(
    private val themeProvider: ThemeProvider,
    private val idPrefix: String
) {

    private var counter = AtomicInteger()

    fun newDividerModel(
        padding: Dp,
        color: Color = themeProvider.theme.colors.dividerOnPrimary
    ): DividerCellModel =
        DividerCellModel(
            id = "$idPrefix${counter.getAndIncrement()}",
            padding = padding,
            color = color
        )
}