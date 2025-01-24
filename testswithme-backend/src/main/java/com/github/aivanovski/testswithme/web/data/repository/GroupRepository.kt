package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.flatten
import arrow.core.raise.either
import com.github.aivanovski.testswithme.domain.tree.TreeBuilder
import com.github.aivanovski.testswithme.domain.tree.findNodeByUid
import com.github.aivanovski.testswithme.domain.tree.getDescendantNodes
import com.github.aivanovski.testswithme.web.data.database.dao.GroupDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.entity.Group
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.DeletedEntityAccessException
import com.github.aivanovski.testswithme.web.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidEntityIdException
import javax.swing.GroupLayout

class GroupRepository(
    private val groupDao: GroupDao,
    private val projectDao: ProjectDao
) {

    fun findByUid(uid: Uid): Either<AppException, Group?> =
        either {
            val group = groupDao.findByUid(uid)

            if (group?.isDeleted == true) {
                raise(DeletedEntityAccessException(Group::class))
            }

            group
        }

    fun getByUid(uid: Uid): Either<AppException, Group> =
        either {
            findByUid(uid).bind()
                ?: raise(FailedToFindEntityByUidException(GroupLayout.Group::class, uid))
        }

    fun getByUserUid(userUid: Uid): Either<AppException, List<Group>> =
        either {
            projectDao.getByUserUid(userUid)
                .map { project ->
                    groupDao.getByProjectUid(project.uid)
                        .filterNotDeleted()
                }
                .flatten()
        }

    fun getByProjectUid(projectUid: Uid): Either<AppException, List<Group>> =
        either {
            groupDao.getAll()
                .filter { group -> group.projectUid == projectUid }
                .filterNotDeleted()
        }

    fun getChildGroups(parentUid: Uid): Either<AppException, List<Group>> =
        either {
            val parent = getByUid(parentUid).bind()

            val allGroups = getByProjectUid(parent.projectUid).bind()

            val root = TreeBuilder.buildTree(
                entities = allGroups,
                uidSelector = { group -> group.uid.toString() },
                parentSelector = { group -> group.parentUid?.toString() }
            )

            val childGroups = root.findNodeByUid(uid = parentUid.toString())
                ?.getDescendantNodes()
                ?.map { node -> node.entity as Group }
                ?: emptyList()

            childGroups
        }

    fun add(group: Group): Either<AppException, Group> =
        either {
            groupDao.add(group)

            getByUid(group.uid).bind()
        }

    fun update(group: Group): Either<AppException, Group> =
        either {
            if (group.id == 0L) {
                raise(InvalidEntityIdException(Group::class))
            }

            groupDao.update(group)
        }

    private fun List<Group>.filterNotDeleted(): List<Group> =
        this.filter { group -> !group.isDeleted }

    companion object {
        const val ROOT_GROUP_NAME = "Root"
    }
}