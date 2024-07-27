package com.github.aivanovski.testwithme.android.presentation.screens.projects

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.presentation.screens.projects.model.ProjectsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectsInteractor(
    private val projectRepository: ProjectRepository
) {

    suspend fun loadData(): Either<AppException, ProjectsData> =
        withContext(Dispatchers.IO) {
            either {
                val projects = projectRepository.getProjects().bind()

                ProjectsData(
                    projects = projects
                )
            }
        }
}