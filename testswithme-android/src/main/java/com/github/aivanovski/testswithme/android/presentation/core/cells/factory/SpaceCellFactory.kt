package com.github.aivanovski.testswithme.android.presentation.core.cells.factory

import androidx.compose.ui.unit.Dp
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.CornersShape
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.ShapedSpaceCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.SpaceCellModel
import java.util.concurrent.atomic.AtomicInteger

class SpaceCellFactory(
    private val idPrefix: String
) {

    private var counter = AtomicInteger()

    fun newSpaceCell(
        height: Dp
    ): SpaceCellModel =
        SpaceCellModel(
            id = "$idPrefix${counter.getAndIncrement()}",
            height = height
        )

    fun newShapedSpaceModel(
        height: Dp,
        shape: CornersShape
    ): ShapedSpaceCellModel =
        ShapedSpaceCellModel(
            id = "$idPrefix${counter.getAndIncrement()}",
            height = height,
            shape = shape
        )
}