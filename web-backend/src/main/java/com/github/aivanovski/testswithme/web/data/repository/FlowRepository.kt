package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.FlowDao
import com.github.aivanovski.testswithme.web.data.database.dao.GroupDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.data.file.FileStorage
import com.github.aivanovski.testswithme.web.data.file.FileStorage.StorageDestination
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.FsPath
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidAccessException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidEntityIdException

class FlowRepository(
    private val flowDao: FlowDao,
    private val projectDao: ProjectDao,
    private val groupDao: GroupDao,
    private val fileStorage: FileStorage
) {

    fun getFlowContent(uid: Uid): Either<AppException, String> =
        either {
            val flow = flowDao.findByUid(uid)
                ?: raise(EntityNotFoundByUidException(Flow::class, uid))

            fileStorage.getContent(
                destination = StorageDestination.FLOWS,
                path = flow.path
            ).bind()
        }

    fun findByFlowUid(uid: Uid): Either<AppException, Flow?> =
        either {
            flowDao.findByUid(uid)
        }

    fun getFlowsByUserUid(userUid: Uid): Either<AppException, List<Flow>> =
        either {
            val projectUids = projectDao.getByUserUid(userUid)
                .map { project -> project.uid }
                .toSet()

            val flows = flowDao.getAll()
                .filter { flow -> flow.projectUid in projectUids }

            flows
        }

    fun getFlowsByProjectAndGroup(
        userUid: Uid,
        projectUid: Uid,
        groupUid: Uid?
    ): Either<AppException, List<Flow>> =
        either {
            val project = projectDao.findByUid(projectUid)
                ?: raise(EntityNotFoundByUidException(Project::class, projectUid))

            if (project.userUid != userUid) {
                raise(InvalidAccessException("Failed to access the project: ${project.name}"))
            }

            val flows = flowDao.getAll()
                .filter { flow -> flow.projectUid == projectUid }
                .filter { flow -> flow.groupUid == groupUid }

            flows
        }

    fun putFlowContent(
        flowUid: Uid,
        projectUid: Uid,
        content: String
    ): Either<AppException, FsPath> =
        either {
            val project = projectDao.findByUid(projectUid)
                ?: raise(EntityNotFoundByUidException(Project::class, projectUid))

            val path = FsPath("${project.name}/$flowUid.yaml")

            fileStorage.putContent(
                destination = StorageDestination.FLOWS,
                path = path,
                content = content
            ).bind()

            path
        }

    fun add(flow: Flow): Either<AppException, Flow> =
        either {
            flowDao.add(flow)
            flowDao.findByUid(flow.uid)
                ?: raise(EntityNotFoundByUidException(Flow::class, flow.uid))
        }

    fun update(flow: Flow): Either<AppException, Flow> =
        either {
            if (flow.id == 0L) {
                raise(InvalidEntityIdException(Flow::class))
            }

            flowDao.update(flow)
        }
}