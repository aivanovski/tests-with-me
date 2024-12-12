package com.github.aivanovski.testswithme.android.presentation.screens.projects

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectsInteractor(
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository
) {

    fun isLoggedInFlow() = authRepository.isLoggedInFlow()

    suspend fun loadData(): Either<AppException, ProjectsData> =
        withContext(Dispatchers.IO) {
            either {
                val projects = projectRepository.getProjects().bind()

                ProjectsData(
                    projects = projects
                )
            }
        }

    fun isLoggedIn(): Boolean = authRepository.isUserLoggedIn()
}