package com.github.aivanovski.testswithme.android.presentation.screens.root

import androidx.lifecycle.ViewModel
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.StartArgs
import com.github.aivanovski.testswithme.android.presentation.core.EventListener
import com.github.aivanovski.testswithme.android.presentation.core.EventProviderImpl
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
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

class RootViewModel(
    private val resourceProvider: ResourceProvider,
    private val settings: Settings,
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
        val isLoggedIn = (settings.authToken != null)

        val screens = mutableListOf<Screen>()

        if (isLoggedIn) {
            if (args.isShowTestRuns) {
                screens.add(Screen.TestRuns)
            } else {
                screens.add(Screen.Projects)
            }
        } else {
            screens.add(Screen.Login(LoginScreenArgs(LoginScreenMode.LOG_IN)))
        }

        return screens
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
        val newIndex = bottomBarState.value.items.indexOf(intent.item)
        bottomBarState.value = bottomBarState.value.copy(
            selectedIndex = newIndex
        )

        when (intent.item) {
            BottomBarItem.PROJECTS -> router.setRoot(Screen.Projects)
            BottomBarItem.TEST_RUNS -> router.setRoot(Screen.TestRuns)
        }
    }

    private fun navigateBack() {
        router.exit()
    }

    private fun onMenuClick(intent: RootIntent.OnMenuClick) {
        menuEventProvider.sendEvent(intent.menuItem)

        when (intent.menuItem) {
            MenuItem.LOG_OUT -> {
                settings.authToken = null
                router.setRoot(Screen.Login(LoginScreenArgs(LoginScreenMode.LOG_IN)))
            }

            else -> {}
        }
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
}