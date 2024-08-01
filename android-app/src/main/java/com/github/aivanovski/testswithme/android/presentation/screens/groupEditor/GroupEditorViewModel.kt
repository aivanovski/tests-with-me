package com.github.aivanovski.testswithme.android.presentation.screens.groupEditor

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.core.BaseViewModel
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorIntent
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorState
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuItem
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.MenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetMenuState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.utils.formatErrorMessage
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GroupEditorViewModel(
    private val interactor: GroupEditorInteractor,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: GroupEditorScreenArgs
) : BaseViewModel() {

    val state = MutableStateFlow(GroupEditorState(isLoading = true))
    private val intents = Channel<GroupEditorIntent>()
    private var isSubscribed = false

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        super.start()

        when (args) {
            is GroupEditorScreenArgs.NewGroup -> {
                rootViewModel.sendIntent(SetTopBarState(createTopBarState()))
                rootViewModel.sendIntent(SetMenuState(MenuState.DONE))
            }

            is GroupEditorScreenArgs.EditGroup -> {
                val topBarState = createTopBarState(title = args.screenTitle)
                rootViewModel.sendIntent(SetTopBarState(topBarState))
                rootViewModel.sendIntent(SetMenuState(MenuState.HIDDEN))
            }
        }

        if (!isSubscribed) {
            isSubscribed = true

            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(GroupEditorIntent.Initialize) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .collect { newState ->
                        state.value = newState
                    }
            }

            rootViewModel.subscribeToMenuEvent(this) { menuItem ->
                handleMenuItemClick(menuItem)
            }
        }
    }

    override fun destroy() {
        super.destroy()
        rootViewModel.unsubscribeFromMenuEvent(this)
    }

    fun sendIntent(intent: GroupEditorIntent) {
        intents.trySend(intent)
    }

    private fun handleIntent(intent: GroupEditorIntent): Flow<GroupEditorState> {
        return when (intent) {
            GroupEditorIntent.Initialize -> loadData()
            GroupEditorIntent.OnDoneMenuClick -> onDoneMenuClicked()
            is GroupEditorIntent.OnNameChanged -> onNameChanged(intent)
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem) {
        when (menuItem) {
            MenuItem.DONE -> sendIntent(GroupEditorIntent.OnDoneMenuClick)
            else -> {}
        }
    }

    private fun onNameChanged(intent: GroupEditorIntent.OnNameChanged): Flow<GroupEditorState> {
        return flowOf(
            state.value.copy(
                name = intent.name,
                nameError = null
            )
        )
    }

    private fun loadData(): Flow<GroupEditorState> {
        return when (args) {
            is GroupEditorScreenArgs.NewGroup -> flowOf(GroupEditorState(isLoading = false))
            is GroupEditorScreenArgs.EditGroup -> loadGroup(args.groupUid)
        }
    }

    private fun loadGroup(groupUid: String): Flow<GroupEditorState> {
        return flow {
            emit(GroupEditorState(isLoading = true))

            val loadGroupResult = interactor.loadGroup(groupUid)
            if (loadGroupResult.isLeft()) {
                val errorMessage = loadGroupResult.unwrapError()
                    .formatErrorMessage(resourceProvider)

                emit(GroupEditorState(errorMessage = errorMessage))
                return@flow
            }

            rootViewModel.sendIntent(SetMenuState(MenuState.DONE))

            val group = loadGroupResult.unwrap()
            emit(GroupEditorState(name = group.name))
        }
    }

    private fun onDoneMenuClicked(): Flow<GroupEditorState> {
        return flow {
            val state = validateInputData()
            if (state.hasErrors()) {
                emit(state)
                return@flow
            }

            emit(GroupEditorState(isLoading = true))

            val uploadResult = uploadGroup(state.name.trim())
            if (uploadResult.isLeft()) {
                val errorMessage = uploadResult.unwrapError()
                    .formatErrorMessage(resourceProvider)

                emit(state.copy(errorMessage = errorMessage))
                return@flow
            }

            val group = uploadResult.unwrap()
            router.setResult(Screen.GroupEditor::class, group)
            router.exit()
        }
    }

    private suspend fun uploadGroup(name: String): Either<AppException, Group> {
        return when (args) {
            is GroupEditorScreenArgs.NewGroup -> {
                interactor.createGroup(
                    request = PostGroupRequest(
                        path = null,
                        projectId = args.projectUid,
                        parentGroupId = args.parentGroupUid,
                        name = name
                    )
                )
            }

            is GroupEditorScreenArgs.EditGroup -> {
                interactor.updateGroup(
                    groupUid = args.groupUid,
                    request = UpdateGroupRequest(
                        parent = null,
                        name = name
                    )
                )
            }
        }
    }

    private fun validateInputData(): GroupEditorState {
        val state = this.state.value

        val nameError = if (state.name.isBlank()) {
            resourceProvider.getString(R.string.should_not_be_empty)
        } else {
            null
        }

        return state.copy(
            errorMessage = null,
            nameError = nameError
        )
    }

    private fun GroupEditorState.hasErrors(): Boolean {
        return nameError != null
    }

    private fun createTopBarState(
        title: String = resourceProvider.getString(R.string.create_group)
    ): TopBarState =
        TopBarState(
            title = title,
            isBackVisible = true
        )
}