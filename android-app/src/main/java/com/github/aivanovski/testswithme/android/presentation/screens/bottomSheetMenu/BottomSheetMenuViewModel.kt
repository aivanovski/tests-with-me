package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu

import androidx.lifecycle.ViewModel
import com.github.aivanovski.testswithme.android.presentation.core.CellIntentProviderImpl
import com.github.aivanovski.testswithme.android.presentation.core.cells.model.MenuCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.cells.BottomSheetMenuCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class BottomSheetMenuViewModel(
    private val cellFactory: BottomSheetMenuCellFactory,
    private val menu: BottomSheetMenu
) : ViewModel() {

    private val intentProvider = CellIntentProviderImpl()
    val state = MutableStateFlow(createState())

    private val _events = Channel<BottomSheetUiEvent>(capacity = Channel.BUFFERED)
    val events: Flow<BottomSheetUiEvent> = _events.receiveAsFlow()

    init {
        intentProvider.subscribe(this) { intent ->
            when (intent) {
                is MenuCellIntent.OnClick -> onMenuItemClick(intent.id)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        intentProvider.clear()
    }

    private fun onMenuItemClick(cellId: String) {
        _events.trySend(BottomSheetUiEvent.OnClick(index = cellId.toInt()))
    }

    private fun createState(): BottomSheetMenuState {
        return BottomSheetMenuState(
            viewModels = cellFactory.createCellViewModels(
                items = menu.items,
                intentProvider = intentProvider
            )
        )
    }
}