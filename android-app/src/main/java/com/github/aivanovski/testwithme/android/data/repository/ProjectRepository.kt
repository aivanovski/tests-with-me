package com.github.aivanovski.testwithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.api.ApiClient
import com.github.aivanovski.testwithme.android.data.db.dao.ProjectEntryDao
import com.github.aivanovski.testwithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testwithme.web.api.request.PostProjectRequest
import com.github.aivanovski.testwithme.web.api.response.PostProjectResponse

class ProjectRepository(
    private val api: ApiClient,
    private val dao: ProjectEntryDao
) {

    suspend fun uploadProject(
        request: PostProjectRequest
    ): Either<AppException, PostProjectResponse> =
        either {
            api.postProject(request).bind()
        }

    suspend fun getProjects(): Either<AppException, List<ProjectEntry>> =
        either {
            val remoteProjects = api.getProjects().bind()
            val uidToLocalProjectMap = dao.getAll()
                .associateBy { project -> project.uid }

            for (remote in remoteProjects) {
                val local = uidToLocalProjectMap[remote.uid]
                if (local != null) {
                    dao.update(remote.copy(id = local.id))
                } else {
                    dao.insert(remote)
                }
            }

            remoteProjects
        }

    suspend fun getProjectByUid(uid: String): Either<AppException, ProjectEntry> =
        either {
            getProjects()
                .bind()
                .firstOrNull { project -> project.uid == uid }
                ?: raise(FailedToFindEntityByUidException(ProjectEntry::class, uid))
        }

    fun getCachedProjectByUid(uid: String): Either<AppException, ProjectEntry> =
        either {
            dao.getByUid(uid)
                ?: raise(FailedToFindEntityByUidException(ProjectEntry::class, uid))
        }
}