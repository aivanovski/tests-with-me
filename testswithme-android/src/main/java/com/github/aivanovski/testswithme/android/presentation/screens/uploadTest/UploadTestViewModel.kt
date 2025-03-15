package com.github.aivanovski.testswithme.android.presentation.screens.uploadTest

import androidx.lifecycle.viewModelScope
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.presentation.core.MviViewModel
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.TerminalState
import com.github.aivanovski.testswithme.android.presentation.core.cells.screen.toTerminalState
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogButton
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.MessageDialogState
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenMode
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.RootIntent.SetTopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.root.model.TopBarState
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestIntent
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestScreenData
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestState
import com.github.aivanovski.testswithme.android.utils.formatErrorMessage
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.web.api.dto.EntityReferenceDto
import com.github.aivanovski.testswithme.web.api.request.PostFlowRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class UploadTestViewModel(
    private val interactor: UploadTestInteractor,
    private val resourceProvider: ResourceProvider,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: UploadTestScreenArgs
) : MviViewModel<UploadTestState, UploadTestIntent>(
    initialState = UploadTestState(terminalState = TerminalState.Loading),
    initialIntent = UploadTestIntent.Initialize
) {

    private val data = MutableStateFlow<UploadTestScreenData?>(null)
    private val newFlowUid = MutableStateFlow<String?>(null)

    override fun start() {
        super.start()

        rootViewModel.sendIntent(SetTopBarState(createInitialTopBarState()))
    }

    override fun handleIntent(
        intent: UploadTestIntent
    ): Flow<UploadTestState> {
        return when (intent) {
            UploadTestIntent.Initialize -> loadData()
            UploadTestIntent.OnUploadButtonClick -> uploadTest(state.value)
            is UploadTestIntent.OnProjectSelected -> onProjectSelected(intent, state.value)
            is UploadTestIntent.OnGroupSelected -> onGroupSelected(intent, state.value)
            is UploadTestIntent.OnDialogActionClick -> onDialogClick(intent)
        }
    }

    private fun onDialogClick(intent: UploadTestIntent.OnDialogActionClick): Flow<UploadTestState> {
        val newFlowUid = newFlowUid.value ?: return emptyFlow()

        when (intent.actionId) {
            ACTION_OK -> {
                viewModelScope.launch {
                    router.exit()
                    router.exit()

                    router.navigateTo(
                        Screen.Flow(
                            args = FlowScreenArgs(
                                mode = FlowScreenMode.Flow(
                                    flowUid = newFlowUid
                                ),
                                screenTitle = StringUtils.EMPTY
                            )
                        )
                    )
                }
            }
        }

        return emptyFlow()
    }

    private fun onProjectSelected(
        intent: UploadTestIntent.OnProjectSelected,
        state: UploadTestState
    ): Flow<UploadTestState> {
        val data = this.data.value ?: return emptyFlow()

        val projects = data.projects.formatProjects()
        val selectedProject = data.projects.findProjectByName(intent.projectName)
            ?: return emptyFlow()

        val groups = data.groups
            .filter { group -> group.projectUid == selectedProject.uid }
            .formatGroups()

        return flowOf(
            state.copy(
                projects = projects,
                selectedProject = selectedProject.name,
                groups = groups,
                selectedGroup = groups.first()
            )
        )
    }

    private fun onGroupSelected(
        intent: UploadTestIntent.OnGroupSelected,
        state: UploadTestState
    ): Flow<UploadTestState> {
        val data = this.data.value ?: return emptyFlow()

        val groups = data.groups.formatGroups()
        val selectedGroup = data.groups.findGroupByName(intent.groupName)
            ?: return emptyFlow()

        return flowOf(
            state.copy(
                groups = groups,
                selectedGroup = selectedGroup.name
            )
        )
    }

    private fun uploadTest(initialState: UploadTestState): Flow<UploadTestState> {
        val data = this.data.value ?: return emptyFlow()
        val project = getSelectedProject() ?: return emptyFlow()

        val group = getSelectedGroup()

        return flow {
            emit(initialState.copy(terminalState = TerminalState.Loading))

            val request = PostFlowRequest(
                parent = EntityReferenceDto(
                    projectId = project.uid,
                    groupId = group?.uid,
                    path = null,
                ),
                base64Content = data.base64Content
            )

            val uploadResult = interactor.uploadFlow(
                flowUid = args.flowUid,
                request = request
            )
            if (uploadResult.isLeft()) {
                val terminalState = uploadResult.unwrapError()
                    .formatErrorMessage(resourceProvider)
                    .toTerminalState()

                emit(initialState.copy(terminalState = terminalState))
                return@flow
            }
            newFlowUid.value = uploadResult.unwrap()

            val dialogState = createUploadSuccessDialog()
            emit(initialState.copy(dialogState = dialogState))
        }
    }

    private fun loadData(): Flow<UploadTestState> {
        return flow {
            emit(UploadTestState(terminalState = TerminalState.Loading))

            val loadDataResult = interactor.loadData(args.flowUid)
            if (loadDataResult.isLeft()) {
                val terminalState = loadDataResult.unwrapError()
                    .formatErrorMessage(resourceProvider)
                    .toTerminalState()

                emit(UploadTestState(terminalState = terminalState))
                return@flow
            }

            data.value = loadDataResult.unwrap()
            val data = loadDataResult.unwrap()

            if (data.projects.isEmpty()) {
                val message = resourceProvider.getString(R.string.no_projects_message)
                emit(UploadTestState(terminalState = TerminalState.Empty(message)))
                return@flow
            }

            val selectedProject = data.projects.first()

            val groups = data.groups
                .filter { group -> group.projectUid == selectedProject.uid }
                .formatGroups()

            emit(
                UploadTestState(
                    projects = data.projects.formatProjects(),
                    groups = groups,
                    selectedProject = selectedProject.name,
                    selectedGroup = groups.first()
                )
            )
        }
    }

    private fun List<ProjectEntry>.formatProjects(): List<String> {
        return this.map { project -> project.name }
    }

    private fun List<GroupEntry>.formatGroups(): List<String> {
        return this.map { group -> group.name }
    }

    private fun getSelectedProject(): ProjectEntry? {
        val data = this.data.value ?: return null

        val name = state.value.selectedProject

        return data.projects.findProjectByName(name)
    }

    private fun getSelectedGroup(): GroupEntry? {
        val data = this.data.value ?: return null

        val name = state.value.selectedGroup

        return if (name == resourceProvider.getString(R.string.root)) {
            null
        } else {
            data.groups.findGroupByName(name)
        }
    }

    private fun List<GroupEntry>.findGroupByName(name: String): GroupEntry? {
        return this.firstOrNull { group -> group.name == name }
    }

    private fun List<ProjectEntry>.findProjectByName(name: String): ProjectEntry? {
        return this.firstOrNull { project -> project.name == name }
    }

    private fun createInitialTopBarState(): TopBarState {
        return TopBarState(
            title = resourceProvider.getString(R.string.upload_test),
            isBackVisible = true
        )
    }

    private fun createUploadSuccessDialog(): MessageDialogState {
        return MessageDialogState(
            title = null,
            message = resourceProvider.getString(R.string.test_successfully_uploaded_message),
            isCancellable = false,
            actionButton = MessageDialogButton.ActionButton(
                title = resourceProvider.getString(R.string.ok),
                actionId = ACTION_OK
            )
        )
    }

    companion object {
        private const val ACTION_OK = 1
    }
}