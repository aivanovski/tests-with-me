package com.github.aivanovski.testswithme.android.presentation.screens.projects

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.domain.usecases.IsUserLoggedInUseCase
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectsInteractor(
    private val projectRepository: ProjectRepository,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
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

    fun isLoggedIn(): Boolean = isUserLoggedInUseCase.isLoggedIn()
}