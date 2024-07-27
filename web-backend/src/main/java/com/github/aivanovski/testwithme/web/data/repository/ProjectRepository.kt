package com.github.aivanovski.testwithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testwithme.web.entity.Project
import com.github.aivanovski.testwithme.web.entity.Uid
import com.github.aivanovski.testwithme.web.entity.exception.AppException
import com.github.aivanovski.testwithme.web.entity.exception.ProjectNotFoundByUidException

class ProjectRepository(
    private val dao: ProjectDao
) {

    fun getAll(): Either<AppException, List<Project>> {
        return Either.Right(dao.getAll())
    }

    fun getByUserUid(userUid: Uid): Either<AppException, List<Project>> =
        either {
            dao.getByUserUid(userUid)
        }

    fun findByUid(uid: Uid): Either<AppException, Project?> {
        return Either.Right(dao.findByUid(uid))
    }

    fun add(project: Project): Either<AppException, Project> =
        either {
            dao.add(project)

            dao.findByUid(project.uid)
                ?: raise(ProjectNotFoundByUidException(project.uid))
        }
}