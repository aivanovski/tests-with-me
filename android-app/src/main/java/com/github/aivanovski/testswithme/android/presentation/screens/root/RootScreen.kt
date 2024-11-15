package com.github.aivanovski.testswithme.android.presentation.screens.root

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testswithme.android.presentation.core.compose.ThemedScreenPreview
import com.github.aivanovski.testswithme.android.presentation.core.compose.events.SingleEventEffect
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testswithme.android.presentation.core.navigation.ScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarItem
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuItem
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootUiEvent
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.utils.StringUtils

@Composable
fun RootScreen(rootComponent: RootScreenComponent) {
    val context = LocalContext.current
    val viewModel = rootComponent.viewModel
    val topBarState by viewModel.topBarState.collectAsState()
    val bottomBarState by viewModel.bottomBarState.collectAsState()
    val menuState by viewModel.menuState.collectAsState()

    RootScreen(
        topBarState = topBarState,
        bottomBarState = bottomBarState,
        menuState = menuState,
        onIntent = viewModel::sendIntent
    ) {
        Children(
            stack = rootComponent.childStack
        ) { (_, component) ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = AppTheme.theme.colors.background
            ) {
                CompositionLocalProvider(
                    LocalViewModelStoreOwner provides component as ViewModelStoreOwner
                ) {
                    (component as ScreenComponent).render()
                }
            }
        }
    }

    SingleEventEffect(
        eventFlow = viewModel.events,
        collector = { event ->
            when (event) {
                is RootUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

@Composable
private fun RootScreen(
    topBarState: TopBarState,
    bottomBarState: BottomBarState,
    menuState: MenuState,
    onIntent: (intent: RootIntent) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarContent(
                state = topBarState,
                menuState = menuState,
                onIntent = onIntent
            )
        },
        bottomBar = {
            BottomBarContent(
                state = bottomBarState,
                onIntent = onIntent
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            color = AppTheme.theme.colors.background
        ) {
            content.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarContent(
    state: TopBarState,
    menuState: MenuState,
    onIntent: (intent: RootIntent) -> Unit
) {
    val onBackClicked = rememberOnClickedCallback {
        onIntent.invoke(RootIntent.NavigateBack)
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.theme.colors.background,
            titleContentColor = AppTheme.theme.colors.primaryText
        ),
        title = {
            Text(
                text = state.title,
                color = AppTheme.theme.colors.primaryText
            )
        },
        navigationIcon = {
            if (state.isBackVisible) {
                IconButton(
                    onClick = onBackClicked
                ) {
                    Icon(
                        tint = AppTheme.theme.colors.primaryIcon,
                        imageVector = AppIcons.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            MenuContent(
                state = menuState,
                onIntent = onIntent
            )
        }
    )
}

@Composable
private fun MenuContent(
    state: MenuState,
    onIntent: (intent: RootIntent) -> Unit
) {
    if (state.items.isEmpty()) {
        return
    }

    var isMenuShown by remember { mutableStateOf(false) }

    for (item in state.items) {
        when (item) {
            MenuItem.DONE -> {
                IconButton(
                    onClick = {
                        onIntent.invoke(RootIntent.OnMenuClick(MenuItem.DONE))
                    }
                ) {
                    Icon(
                        imageVector = AppIcons.Check,
                        tint = AppTheme.theme.colors.primaryIcon,
                        contentDescription = null
                    )
                }
            }

            else -> {
                IconButton(
                    onClick = { isMenuShown = !isMenuShown }
                ) {
                    Icon(
                        imageVector = AppIcons.Menu,
                        tint = AppTheme.theme.colors.primaryIcon,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = isMenuShown,
                    onDismissRequest = {
                        isMenuShown = false
                    }
                ) {
                    for (item in state.items) {
                        DropdownMenuItem(
                            onClick = {
                                onIntent.invoke(RootIntent.OnMenuClick(item))
                            },
                            text = {
                                Text(item.getTitle())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItem.getTitle(): String {
    return when (this) {
        MenuItem.DONE -> StringUtils.EMPTY
    }
}

@Composable
private fun BottomBarContent(
    state: BottomBarState,
    onIntent: (intent: RootIntent) -> Unit
) {
    if (!state.isVisible) {
        return
    }

    var selectedItem by remember { mutableIntStateOf(state.selectedIndex) }

    val onBarClicked = rememberCallback { index: Int ->
        onIntent.invoke(RootIntent.OnBottomBarClick(state.items[index]))
    }

    NavigationBar {
        state.items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.getIcon(),
                        contentDescription = null
                    )
                },
                label = { Text(item.getTitle()) },
                selected = selectedItem == index,
                onClick = {
                    if (state.items[index] != BottomBarItem.MORE) {
                        selectedItem = index
                    }
                    onBarClicked.invoke(index)
                }
            )
        }
    }
}

private fun BottomBarItem.getIcon(): ImageVector {
    return when (this) {
        BottomBarItem.PROJECTS -> AppIcons.MenuProjects
        BottomBarItem.TEST_RUNS -> AppIcons.MenuTestRuns
        BottomBarItem.MORE -> AppIcons.Menu
    }
}

@Composable
private fun BottomBarItem.getTitle(): String {
    return when (this) {
        BottomBarItem.PROJECTS -> stringResource(R.string.projects)
        BottomBarItem.TEST_RUNS -> stringResource(R.string.test_runs)
        BottomBarItem.MORE -> stringResource(R.string.more)
    }
}

@Composable
@Preview
fun RootScreenLightPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        RootScreen(
            topBarState = newTopBarState(),
            bottomBarState = newBottomBarState(),
            menuState = newMenuState(),
            onIntent = {},
            content = {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text("SCREEN CONTENT")
                }
            }
        )
    }
}

private fun newTopBarState(): TopBarState =
    TopBarState(
        title = "Top Bar Title",
        isBackVisible = true
    )

private fun newMenuState(): MenuState =
    MenuState(
        items = listOf(
            MenuItem.DONE
        )
    )

private fun newBottomBarState(): BottomBarState =
    BottomBarState(
        isVisible = true,
        selectedIndex = 0,
        items = listOf(
            BottomBarItem.PROJECTS,
            BottomBarItem.TEST_RUNS,
            BottomBarItem.MORE
        )
    )