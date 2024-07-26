package com.github.aivanovski.testwithme.android.presentation.screens.projectEditor

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testwithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.web.api.request.PostProjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectEditorInteractor(
    private val projectRepository: ProjectRepository
) {

    suspend fun upload(
        request: PostProjectRequest
    ): Either<AppException, ProjectEntry> = withContext(Dispatchers.IO) {
        either {
            val response = projectRepository.uploadProject(request).bind()

            projectRepository.getProjectByUid(response.id).bind()
        }
    }
}