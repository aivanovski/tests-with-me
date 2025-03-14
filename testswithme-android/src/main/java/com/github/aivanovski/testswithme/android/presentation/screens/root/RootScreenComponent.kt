package com.github.aivanovski.testswithme.android.presentation.screens.root

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.backhandler.BackCallback
import com.github.aivanovski.testswithme.android.presentation.StartArgs
import com.github.aivanovski.testswithme.android.presentation.core.ViewModelFactory
import com.github.aivanovski.testswithme.android.presentation.core.decompose.ViewModelStoreOwnerImpl
import com.github.aivanovski.testswithme.android.presentation.core.navigation.RouterImpl
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.FlowScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.GroupEditorScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.groups.GroupsScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.login.LoginScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.ProjectDashboardScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.ProjectEditorScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.projects.ProjectsScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.ResetRunsScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.settings.SettingsScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.TestContentScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.testRuns.TestRunsScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.TextViewerScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.UploadTestScreenComponent

class RootScreenComponent(
    componentContext: ComponentContext,
    onExitNavigation: () -> Unit,
    fragmentManager: FragmentManager,
    args: StartArgs
) : ComponentContext by componentContext,
    ViewModelStoreOwner by ViewModelStoreOwnerImpl() {

    // TODO: ViewModels instances should be retain in case of screen state restoration

    val navigation = StackNavigation<Screen>()
    val router = RouterImpl(
        rootComponent = this,
        fragmentManager = fragmentManager,
        onExitNavigation = onExitNavigation
    )

    val viewModel: RootViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = ViewModelFactory(router, args)
        )[RootViewModel::class]
    }

    val childStack = childStack(
        source = navigation,
        serializer = Screen.serializer(),
        initialStack = { viewModel.getStartScreens() },
        childFactory = { screen, childContext -> createScreenComponent(screen, childContext) }
    )

    private val backCallback = BackCallback(
        isEnabled = true,
        onBack = {
            router.exit()
        }
    )

    init {
        backHandler.register(backCallback)
    }

    private fun createScreenComponent(
        screen: Screen,
        childContext: ComponentContext
    ): ComponentContext {
        return when (screen) {
            is Screen.Login ->
                LoginScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.Projects ->
                ProjectsScreenComponent(childContext, viewModel, router)

            is Screen.ProjectEditor ->
                ProjectEditorScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.Groups ->
                GroupsScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.GroupEditor ->
                GroupEditorScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.Flow ->
                FlowScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.TestRuns ->
                TestRunsScreenComponent(childContext, viewModel, router)

            is Screen.UploadTest ->
                UploadTestScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.ProjectDashboard ->
                ProjectDashboardScreenComponent(childContext, viewModel, router, screen.args)

            Screen.Settings ->
                SettingsScreenComponent(childContext, viewModel, router)

            is Screen.ResetRuns ->
                ResetRunsScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.TextViewer ->
                TextViewerScreenComponent(childContext, viewModel, router, screen.args)

            is Screen.TestContent ->
                TestContentScreenComponent(childContext, viewModel, router, screen.args)
        }
    }
}