package com.github.aivanovski.testswithme.android.presentation.screens.projectEditor

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.web.api.request.PostProjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectEditorInteractor(
    private val projectRepository: ProjectRepository
) {

    suspend fun upload(request: PostProjectRequest): Either<AppException, ProjectEntry> =
        withContext(Dispatchers.IO) {
            either {
                val response = projectRepository.uploadProject(request).bind()

                projectRepository.getProjectByUid(response.id).bind()
            }
        }
}