package com.github.aivanovski.testswithme.android.presentation.screens.login.cells

import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextChipItem
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.TextSize
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.UnshapedChipRowCellModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.viewModel.UnshapedChipRowCellViewModel
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider

class LoginCellFactory(
    private val resourceProvider: ResourceProvider,
    private val themeProvider: ThemeProvider
) {

    fun createUsersCell(
        users: List<String>,
        intentProvider: CellIntentProvider
    ): UnshapedChipRowCellViewModel {
        val model = UnshapedChipRowCellModel(
            id = CellId.USERS_CELL,
            chips = users.map { user ->
                TextChipItem(
                    text = user,
                    textColor = themeProvider.theme.colors.primaryText,
                    textSize = TextSize.TITLE,
                    isClickable = true,
                    isSelected = false
                )
            }
        )

        return UnshapedChipRowCellViewModel(
            model = model,
            intentProvider = intentProvider
        )
    }

    object CellId {
        const val USERS_CELL = "users-cell"
    }
}