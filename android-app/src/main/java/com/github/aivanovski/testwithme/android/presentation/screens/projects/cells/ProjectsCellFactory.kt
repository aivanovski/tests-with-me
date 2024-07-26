package com.github.aivanovski.testwithme.android.presentation.screens.projects.cells

import com.github.aivanovski.testwithme.android.domain.resources.ResourceProvider
import com.github.aivanovski.testwithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testwithme.android.entity.exception.UnsupportedCellModelException
import com.github.aivanovski.testwithme.android.presentation.core.CellIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.BaseCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.model.SpaceCellModel
import com.github.aivanovski.testwithme.android.presentation.core.cells.viewModel.SpaceCellViewModel
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.SmallMargin
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.model.ProjectCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.viewModel.ProjectCellViewModel

class ProjectsCellFactory(
    private val resourceProvider: ResourceProvider
) {

    fun createCellViewModels(
        projects: List<ProjectEntry>,
        intentProvider: CellIntentProvider
    ): List<BaseCellViewModel> {
        return createModels(projects).map { model ->
            when (model) {
                is ProjectCellModel -> ProjectCellViewModel(model, intentProvider)
                is SpaceCellModel -> SpaceCellViewModel(model)
                else -> throw UnsupportedCellModelException(model)
            }
        }
    }

    private fun createModels(
        projects: List<ProjectEntry>
    ): List<BaseCellModel> {
        val models = mutableListOf<BaseCellModel>()

        models.add(SpaceCellModel(ElementMargin))

        projects.forEachIndexed { index, project ->
            if (index > 0) {
                models.add(SpaceCellModel(SmallMargin))
            }

            models.add(
                ProjectCellModel(
                    id = project.uid,
                    title = project.name,
                    description = project.description,
                    iconUrl = project.imageUrl
                )
            )
        }

        models.add(SpaceCellModel(ElementMargin))

        return models
    }
}