package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException

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

    fun findByUid(uid: Uid): Either<AppException, Project?> =
        either {
            dao.findByUid(uid)
        }

    fun getByUid(uid: Uid): Either<AppException, Project> =
        either {
            findByUid(uid).bind()
                ?: raise(EntityNotFoundByUidException(Project::class, uid))
        }

    fun add(project: Project): Either<AppException, Project> =
        either {
            dao.add(project)

            getByUid(project.uid).bind()
        }
}