package com.github.aivanovski.testswithme.android.presentation.screens.projects

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.BaseCellIntent
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toTerminalState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projects.cells.ProjectsCellFactory
import com.github.aivanovski.testswithme.android.presentation.screens.projects.cells.model.ProjectCellIntent
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsData
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsIntent
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsState
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.BottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetBottomBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatError
import com.github.aivanovski.testswithme.extensions.unwrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val interactor: ProjectsInteractor,
    private val cellFactory: ProjectsCellFactory,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router
) : BaseViewModel() {

    val state = MutableStateFlow(ProjectsState(terminalState = TerminalState.Loading))
    private val intents = Channel<ProjectsIntent>()
    private var data: ProjectsData? = null
    private var isSubscribed = false

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetBottomBarState(createBottomBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(ProjectsIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent, state.value) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }

            viewModelScope.launch {
                interactor.isLoggedInFlow()
                    .collect {
                        intents.trySend(ProjectsIntent.ReloadData)
                    }
            }
        }
    }

    override fun handleCellIntent(intent: BaseCellIntent) {
        when (intent) {
            is ProjectCellIntent.OnClick -> {
                navigateToProjectDashboardScreen(projectUid = intent.id)
            }
        }
    }

    fun sendIntent(intent: ProjectsIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(
        intent: ProjectsIntent,
        state: ProjectsState
    ): Flow<ProjectsState> {
        return when (intent) {
            ProjectsIntent.Initialize -> loadData()
            ProjectsIntent.ReloadData -> loadData()
            ProjectsIntent.OnAddButtonClick -> {
                navigateToNewProjectScreen()
                emptyFlow()
            }
        }
    }

    private fun navigateToNewProjectScreen() {
        router.navigateTo(
            Screen.ProjectEditor(
                ProjectEditorArgs.NewProject
            )
        )
        router.setResultListener(Screen.ProjectEditor::class) { _ ->
            sendIntent(ProjectsIntent.ReloadData)
        }
    }

    private fun navigateToProjectDashboardScreen(projectUid: String) {
        val project = data?.projects
            ?.firstOrNull { project -> project.uid == projectUid }
            ?: return

        router.navigateTo(
            Screen.ProjectDashboard(
                ProjectDashboardScreenArgs(
                    screenTitle = project.name,
                    projectUid = project.uid
                )
            )
        )
    }

    private fun loadData(): Flow<ProjectsState> {
        return interactor.loadData()
            .map { loadDataResult ->
                if (!interactor.isLoggedIn()) {
                    return@map ProjectsState(
                        terminalState = TerminalState.Empty(
                            message = resourceProvider.getString(R.string.not_logged_in_message)
                        )
                    )
                }

                if (loadDataResult.isLeft()) {
                    val state = loadDataResult
                        .formatError(resourceProvider)
                        .toTerminalState()
                    return@map ProjectsState(terminalState = state)
                }

                data = loadDataResult.unwrap()
                val data = loadDataResult.unwrap()

                if (data.projects.isNotEmpty()) {
                    val viewModels = cellFactory.createCellViewModels(
                        projects = data.projects,
                        intentProvider = intentProvider
                    )

                    ProjectsState(
                        viewModels = viewModels,
                        isAddButtonVisible = true
                    )
                } else {
                    val emptyState = TerminalState.Empty(
                        message = resourceProvider.getString(R.string.no_projects_message)
                    )
                    ProjectsState(
                        terminalState = emptyState,
                        isAddButtonVisible = true
                    )
                }
            }
            .onStart {
                ProjectsState(terminalState = TerminalState.Loading)
            }
    }

    private fun createTopBarState(): TopBarState {
        return TopBarState(
            title = resourceProvider.getString(R.string.projects),
            isBackVisible = false
        )
    }

    private fun createBottomBarState(): BottomBarState {
        return rootViewModel.bottomBarState.value.copy(
            isVisible = true,
            selectedIndex = 0
        )
    }
}