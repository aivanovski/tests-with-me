package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.db.dao.ProjectEntryDao
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.web.api.request.PostProjectRequest
import com.github.aivanovski.testswithme.web.api.response.PostProjectResponse

class ProjectRepository(
    private val api: ApiClient,
    private val projectDao: ProjectEntryDao,
    private val authRepository: AuthRepository
) {

    suspend fun uploadProject(
        request: PostProjectRequest
    ): Either<AppException, PostProjectResponse> =
        either {
            api.postProject(request).bind()
        }

    suspend fun getProjects(): Either<AppException, List<ProjectEntry>> =
        either {
            if (!authRepository.isUserLoggedIn()) {
                return@either projectDao.getAll()
            }

            val remoteProjects = api.getProjects().bind()
            val uidToLocalProjectMap = projectDao.getAll()
                .associateBy { project -> project.uid }

            for (remote in remoteProjects) {
                val local = uidToLocalProjectMap[remote.uid]
                if (local != null) {
                    projectDao.update(remote.copy(id = local.id))
                } else {
                    projectDao.insert(remote)
                }
            }

            projectDao.getAll()
        }

    suspend fun getProjectByUid(uid: String): Either<AppException, ProjectEntry> =
        either {
            getProjects().bind()
                .firstOrNull { project -> project.uid == uid }
                ?: raise(FailedToFindEntityByUidException(ProjectEntry::class, uid))
        }

    fun getCachedProjectByUid(uid: String): Either<AppException, ProjectEntry> =
        either {
            projectDao.getByUid(uid)
                ?: raise(FailedToFindEntityByUidException(ProjectEntry::class, uid))
        }

    fun clear() {
        projectDao.removeAll()
    }
}