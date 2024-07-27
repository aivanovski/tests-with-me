package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.flatten
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.GroupDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException

class GroupRepository(
    private val groupDao: GroupDao,
    private val projectDao: ProjectDao
) {

    fun findByUid(uid: Uid): Either<AppException, Group?> {
        return Either.Right(groupDao.findByUid(uid))
    }

    fun getByUserUid(userUid: Uid): Either<AppException, List<Group>> =
        either {
            projectDao.getByUserUid(userUid)
                .map { project ->
                    groupDao.getByProjectUid(project.uid)
                }
                .flatten()
        }

    fun getByProjectUid(projectUid: Uid): Either<AppException, List<Group>> =
        either {
            groupDao.getAll()
                .filter { group -> group.projectUid == projectUid }
        }

    fun add(group: Group): Either<AppException, Group> =
        either {
            groupDao.add(group)

            groupDao.findByUid(group.uid)
                ?: raise(EntityNotFoundByUidException(Group::class, group.uid))
        }
}