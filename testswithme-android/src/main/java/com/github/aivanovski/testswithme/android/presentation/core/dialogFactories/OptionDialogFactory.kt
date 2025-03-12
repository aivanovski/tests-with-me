package com.github.aivanovski.testswithme.android.presentation.core.dialogFactories

import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.DialogAction
import com.github.aivanovski.testswithme.android.presentation.core.compose.dialogs.model.OptionDialogState

object OptionDialogFactory {

    const val ACTION_OPEN_DOWNLOADS_PAGE = 1001
    const val ACTION_OPEN_WEBSITE = 1002

    const val ACTION_RESET_PROGRESS = 2001

    fun createApplicationOptionsDialog(
        project: ProjectEntry?,
        resourceProvider: ResourceProvider
    ): OptionDialogState {
        val optionsAndActions = mutableListOf(
            Pair(
                resourceProvider.getString(R.string.open_downloads_page),
                ACTION_OPEN_DOWNLOADS_PAGE
            )
        )

        if (project?.siteUrl != null) {
            optionsAndActions.add(
                Pair(
                    resourceProvider.getString(R.string.open_project_website),
                    ACTION_OPEN_WEBSITE
                )
            )
        }

        return OptionDialogState(
            options = optionsAndActions.map { (option, _) -> option },
            actions = optionsAndActions.map { (_, action) -> DialogAction(action) }
        )
    }

    fun createProgressOptionDialog(resourceProvider: ResourceProvider): OptionDialogState {
        val options = listOf(
            resourceProvider.getString(R.string.reset_progress)
        )
        val actions = listOf(
            DialogAction(ACTION_RESET_PROGRESS)
        )

        return OptionDialogState(
            options = options,
            actions = actions
        )
    }
}