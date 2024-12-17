package com.github.aivanovski.testswithme.android.presentation.screens.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.StartArgs
import com.github.aivanovski.testswithme.android.presentation.core.EventListener
import com.github.aivanovski.testswithme.android.presentation.core.EventProviderImpl
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.BottomSheetMenu
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetIcon
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetItem
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarItem
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuItem
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootUiEvent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val interactor: RootInteractor,
    private val resourceProvider: ResourceProvider,
    private val router: Router,
    private val args: StartArgs
) : ViewModel() {

    val topBarState = MutableStateFlow(newDefaultState())
    val bottomBarState = MutableStateFlow(BottomBarState.HIDDEN)
    val menuState = MutableStateFlow(MenuState.HIDDEN)

    private val _events = Channel<RootUiEvent>(capacity = Channel.BUFFERED)
    val events: Flow<RootUiEvent> = _events.receiveAsFlow()

    private val menuEventProvider = EventProviderImpl<MenuItem>()

    fun getStartScreens(): List<Screen> {
        return if (args.isShowTestRuns) {
            listOf(Screen.TestRuns)
        } else {
            listOf(Screen.Projects)
        }
    }

    fun sendIntent(intent: RootIntent) {
        handleIntent(intent)
    }

    fun subscribeToMenuEvent(
        subscriber: Any,
        listener: EventListener<MenuItem>
    ) {
        menuEventProvider.subscribe(subscriber, listener)
    }

    fun unsubscribeFromMenuEvent(subscriber: Any) {
        menuEventProvider.unsubscribe(subscriber)
    }

    private fun handleIntent(intent: RootIntent) {
        when (intent) {
            RootIntent.NavigateBack -> navigateBack()
            is RootIntent.SetTopBarState -> setTopBarState(intent)
            is RootIntent.SetBottomBarState -> setBottomBarState(intent)
            is RootIntent.SetMenuState -> setMenuState(intent)
            is RootIntent.OnBottomBarClick -> onBottomBarClick(intent)
            is RootIntent.OnMenuClick -> onMenuClick(intent)
            is RootIntent.ShowToast -> showToast(intent)
        }
    }

    private fun setTopBarState(intent: RootIntent.SetTopBarState) {
        topBarState.value = intent.state
    }

    private fun setBottomBarState(intent: RootIntent.SetBottomBarState) {
        bottomBarState.value = intent.state
    }

    private fun setMenuState(intent: RootIntent.SetMenuState) {
        menuState.value = intent.state
    }

    private fun onBottomBarClick(intent: RootIntent.OnBottomBarClick) {
        if (intent.item != BottomBarItem.MORE) {
            val newIndex = bottomBarState.value.items.indexOf(intent.item)
            bottomBarState.value = bottomBarState.value.copy(
                selectedIndex = newIndex
            )
        }

        when (intent.item) {
            BottomBarItem.PROJECTS -> router.setRoot(Screen.Projects)
            BottomBarItem.TEST_RUNS -> router.setRoot(Screen.TestRuns)
            BottomBarItem.MORE -> showMoreMenu()
        }
    }

    private fun navigateBack() {
        router.exit()
    }

    private fun onMenuClick(intent: RootIntent.OnMenuClick) {
        menuEventProvider.sendEvent(intent.menuItem)
    }

    private fun showToast(intent: RootIntent.ShowToast) {
        _events.trySend(RootUiEvent.ShowToast(intent.message))
    }

    private fun newDefaultState(): TopBarState {
        return TopBarState(
            title = resourceProvider.getString(R.string.app_name),
            isBackVisible = false
        )
    }

    private fun showMoreMenu() {
        val menu = createMoreMenu()

        router.showBottomSheet(
            menu = createMoreMenu(),
            onClick = { index ->
                val item = MoreMenuItem.valueOf(menu.items[index].id)
                onMoreMenuClicked(item)
            }
        )
    }

    private fun createMoreMenu(): BottomSheetMenu {
        val items = mutableListOf<BottomSheetItem>()
            .apply {
                if (interactor.isUserLoggedIn()) {
                    add(
                        BottomSheetItem(
                            id = MoreMenuItem.LOGOUT.name,
                            icon = BottomSheetIcon.LOGOUT,
                            title = resourceProvider.getString(R.string.log_out)
                        )
                    )
                } else {
                    add(
                        BottomSheetItem(
                            id = MoreMenuItem.LOGIN.name,
                            icon = BottomSheetIcon.LOGIN,
                            title = resourceProvider.getString(R.string.log_in_join)
                        )
                    )
                }

                add(
                    BottomSheetItem(
                        id = MoreMenuItem.SETTINGS.name,
                        icon = BottomSheetIcon.SETTINGS,
                        title = resourceProvider.getString(R.string.settings)
                    )
                )
            }

        return BottomSheetMenu(items = items)
    }

    private fun onMoreMenuClicked(item: MoreMenuItem) {
        when (item) {
            MoreMenuItem.LOGIN -> {
                router.navigateTo(
                    Screen.Login(
                        args = LoginScreenArgs(
                            mode = LoginScreenMode.LOG_IN
                        )
                    )
                )
            }

            MoreMenuItem.LOGOUT -> {
                viewModelScope.launch {
                    interactor.logout()
                }
            }

            MoreMenuItem.SETTINGS -> {
                router.navigateTo(Screen.Settings)
            }
        }
    }

    enum class MoreMenuItem {
        LOGIN,
        LOGOUT,
        SETTINGS
    }
}