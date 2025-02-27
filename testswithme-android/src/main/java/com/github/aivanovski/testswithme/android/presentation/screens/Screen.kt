package com.github.aivanovski.testswithme.android.presentation.screens

import com.github.aivanovski.testswithme.android.presentation.screens.flow.model.FlowScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model.GroupEditorScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.groups.model.GroupsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.login.model.LoginScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projectDashboard.model.ProjectDashboardScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model.ProjectEditorArgs
import com.github.aivanovski.testswithme.android.presentation.screens.resetRuns.model.ResetRunsScreenArgs
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentArgs
import com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model.TextViewerArgs
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestScreenArgs
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data class Login(
        val args: LoginScreenArgs
    ) : Screen

    @Serializable
    data object Projects : Screen

    @Serializable
    data class ProjectEditor(
        val args: ProjectEditorArgs
    ) : Screen

    @Serializable
    data object TestRuns : Screen

    @Serializable
    data class ProjectDashboard(
        val args: ProjectDashboardScreenArgs
    ) : Screen

    @Serializable
    data class Groups(
        val args: GroupsScreenArgs
    ) : Screen

    @Serializable
    data class GroupEditor(
        val args: GroupEditorScreenArgs
    ) : Screen

    @Serializable
    data class Flow(
        val args: FlowScreenArgs
    ) : Screen

    @Serializable
    data class UploadTest(
        val args: UploadTestScreenArgs
    ) : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data class ResetRuns(
        val args: ResetRunsScreenArgs
    ) : Screen

    @Serializable
    data class TextViewer(
        val args: TextViewerArgs
    ) : Screen

    @Serializable
    data class TestContent(
        val args: TestContentArgs
    ) : Screen
}