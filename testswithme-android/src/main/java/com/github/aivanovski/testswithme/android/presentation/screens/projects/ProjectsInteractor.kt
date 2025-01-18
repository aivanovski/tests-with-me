package com.github.aivanovski.testswithme.android.presentation.screens.projects

import arrow.core.Either
import arrow.core.right
import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.screens.projects.model.ProjectsData
import com.github.aivanovski.testswithme.extensions.remapError
import com.github.aivanovski.testswithme.extensions.unwrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectsInteractor(
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository
) {

    fun isLoggedInFlow() = authRepository.isLoggedInFlow()

    fun loadData(): Flow<Either<AppException, ProjectsData>> =
        projectRepository.getProjectsFlow()
            .map { getProjectsResult ->
                if (getProjectsResult.isLeft()) {
                    return@map getProjectsResult.remapError()
                }

                ProjectsData(
                    projects = getProjectsResult.unwrap()
                ).right()
            }

    fun isLoggedIn(): Boolean = authRepository.isUserLoggedIn()
}