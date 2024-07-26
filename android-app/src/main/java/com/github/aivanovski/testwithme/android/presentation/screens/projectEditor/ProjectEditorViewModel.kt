package com.github.aivanovski.testwithme.android.presentation.screens.projectEditor

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testwithme.android.R
import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testwithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testwithme.android.presentation.screens.Screen
import com.github.aivanovski.testwithme.android.presentation.screens.projectEditor.model.ProjectEditorArgs
import com.github.aivanovski.testwithme.android.presentation.screens.projectEditor.model.ProjectEditorIntent
import com.github.aivanovski.testwithme.android.presentation.screens.projectEditor.model.ProjectEditorState
import com.github.aivanovski.testwithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testwithme.android.presentation.screens.root.model.MenuItem
import com.github.aivanovski.testwithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testwithme.android.presentation.screens.root.model.RootIntent
import com.github.aivanovski.testwithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testwithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testwithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testwithme.android.utils.formatErrorMessage
import com.github.aivanovski.testwithme.extensions.unwrap
import com.github.aivanovski.testwithme.extensions.unwrapError
import com.github.aivanovski.testwithme.web.api.request.PostProjectRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProjectEditorViewModel(
    private val interactor: ProjectEditorInteractor,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: ProjectEditorArgs
) : BaseViewModel() {

    val state = MutableStateFlow(newLoadingState())
    private val intents = Channel<ProjectEditorIntent>()

    private var isSubscribed = false

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        super.start()
        rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
        rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(ProjectEditorIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .collect { newState ->
                        state.value = newState
                    }
            }

            rootViewModel.subscribeToMenuEvent(this) { menuItem ->
                onMenuItemClicked(menuItem)
            }
        }
    }

    override fun destroy() {
        super.destroy()
        rootViewModel.unsubscribeFromMenuEvent(this)
    }

    fun sendIntent(intent: ProjectEditorIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(
        intent: ProjectEditorIntent
    ): Flow<ProjectEditorState> {
        return when (intent) {
            ProjectEditorIntent.Initialize -> loadData()
            is ProjectEditorIntent.OnDoneMenuClick -> onDoneMenuClicked()
            is ProjectEditorIntent.OnPackageNameChanged -> onPackageNameChanged(intent)
            is ProjectEditorIntent.OnNameChanged -> onNameChanged(intent)
            is ProjectEditorIntent.OnDescriptionChanged -> onDescriptionChanged(intent)
            is ProjectEditorIntent.OnSiteUrlChanged -> onSiteUrlChanged(intent)
            is ProjectEditorIntent.OnDownloadUrlChanged -> onDownloadUrlChanged(intent)
            is ProjectEditorIntent.OnMessageDialogClick -> {
                router.exit()
                emptyFlow()
            }
        }
    }

    private fun onMenuItemClicked(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.DONE -> sendIntent(ProjectEditorIntent.OnDoneMenuClick)
            else -> {}
        }
    }

    private fun onDoneMenuClicked(): Flow<ProjectEditorState> {
        return flow {
            val state = validateData()
            if (state.hasErrors()) {
                emit(state)
                return@flow
            }

            emit(state.copy(isLoading = true))
            val request = PostProjectRequest(
                packageName = state.packageName.trim(),
                name = state.name.trim(),
                description = state.description.trim(),
                siteUrl = state.siteUrl.trim(),
                downloadUrl = state.downloadUrl.trim(),
                imageUrl = null
            )

            val uploadResult = interactor.upload(request)
            if (uploadResult.isLeft()) {
                val errorMessage = uploadResult.unwrapError()
                    .formatErrorMessage(resourceProvider)

                emit(state.copy(errorMessage = errorMessage))
                return@flow
            }

            val project = uploadResult.unwrap()

            rootViewModel.sendIntent(
                RootIntent.ShowToast(
                    message = resourceProvider.getString(
                        R.string.project_successfully_created_with_str,
                        project.name
                    )
                )
            )
            router.setResult(Screen.ProjectEditor::class, project)
            router.exit()
        }
    }

    private fun onPackageNameChanged(
        intent: ProjectEditorIntent.OnPackageNameChanged
    ): Flow<ProjectEditorState> {
        return flowOf(
            state.value.copy(
                packageName = intent.packageName,
                packageNameError = null
            )
        )
    }

    private fun onNameChanged(
        intent: ProjectEditorIntent.OnNameChanged
    ): Flow<ProjectEditorState> {
        return flowOf(
            state.value.copy(
                name = intent.name,
                nameError = null
            )
        )
    }

    private fun onDescriptionChanged(
        intent: ProjectEditorIntent.OnDescriptionChanged
    ): Flow<ProjectEditorState> {
        return flowOf(
            state.value.copy(
                description = intent.description
            )
        )
    }

    private fun onSiteUrlChanged(
        intent: ProjectEditorIntent.OnSiteUrlChanged
    ): Flow<ProjectEditorState> {
        return flowOf(
            state.value.copy(
                siteUrl = intent.siteUrl
            )
        )
    }

    private fun onDownloadUrlChanged(
        intent: ProjectEditorIntent.OnDownloadUrlChanged
    ): Flow<ProjectEditorState> {
        return flowOf(
            state.value.copy(
                downloadUrl = intent.downloadUrl
            )
        )
    }

    private fun loadData(): Flow<ProjectEditorState> {
        return flow {
            when (args) {
                is ProjectEditorArgs.NewProject -> {
                    emit(ProjectEditorState(isLoading = false))
                    rootViewModel.sendIntent(SetMenuState(MenuState.DONE))
                }

                is ProjectEditorArgs.EditProject -> {
                    emit(ProjectEditorState(isLoading = true))
                }
            }
        }
    }

    private fun validateData(): ProjectEditorState {
        val state = this.state.value

        val packageNameError = if (state.packageName.isBlank()) {
            resourceProvider.getString(R.string.should_not_be_empty)
        } else {
            null
        }

        val nameError = if (state.name.isBlank()) {
            resourceProvider.getString(R.string.should_not_be_empty)
        } else {
            null
        }

        return state.copy(
            errorMessage = null,
            packageNameError = packageNameError,
            nameError = nameError
        )
    }

    private fun ProjectEditorState.hasErrors(): Boolean {
        return packageNameError != null || nameError != null
    }

    private fun newLoadingState(): ProjectEditorState =
        ProjectEditorState(isLoading = true)

    private fun createTopBarState(): TopBarState =
        TopBarState(
            title = resourceProvider.getString(R.string.create_project),
            isBackVisible = true
        )

    companion object {
        private const val ACTION_OK = 1
    }
}