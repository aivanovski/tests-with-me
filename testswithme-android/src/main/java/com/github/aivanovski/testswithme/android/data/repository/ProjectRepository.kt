package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.db.dao.ProjectEntryDao
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.web.api.request.PostProjectRequest
import com.github.aivanovski.testswithme.web.api.response.PostProjectResponse
import com.github.aivanovski.testswithme.web.api.response.RequestProjectSyncResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    fun getProjectsFlow(): Flow<Either<AppException, List<ProjectEntry>>> =
        flow {
            val localProjects = projectDao.getAll()

            if (!authRepository.isUserLoggedIn()) {
                emit(localProjects.right())
                return@flow
            }

            if (localProjects.isNotEmpty()) {
                emit(localProjects.right())
            }

            val getProjectsResult = api.getProjects()
            if (getProjectsResult.isLeft()) {
                if (localProjects.isEmpty()) {
                    emit(localProjects.right())
                }
                return@flow
            }

            val remoteProjects = getProjectsResult.unwrap()

            mergeEntities(
                localEntities = localProjects,
                remoteEntities = remoteProjects,
                entityToUidMapper = { project -> project.uid },
                onInsert = { project -> projectDao.insert(project) },
                onUpdate = { local, remote -> projectDao.update(remote.copy(id = local.id)) },
                onDelete = { project -> projectDao.removeByUid(project.uid) }
            )

            emit(projectDao.getAll().right())
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

    suspend fun requestSync(
        projectUid: String
    ): Either<AppException, RequestProjectSyncResponse> =
        api.requestProjectSync(projectUid)
}