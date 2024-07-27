package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.FlowDao
import com.github.aivanovski.testswithme.web.data.database.dao.FlowRunDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.data.file.FileStorage
import com.github.aivanovski.testswithme.web.data.file.FileStorage.StorageDestination
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.FlowRun
import com.github.aivanovski.testswithme.web.entity.FsPath
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException

class FlowRunRepository(
    private val flowRunDao: FlowRunDao,
    private val flowDao: FlowDao,
    private val projectDao: ProjectDao,
    private val fileStorage: FileStorage
) {

    fun getAll(): Either<AppException, List<FlowRun>> {
        return Either.Right(flowRunDao.getAll())
    }

    fun findByUid(uid: Uid): Either<AppException, FlowRun?> =
        either {
            flowRunDao.findByUid(uid)
        }

    fun getByUserUid(userUid: Uid): Either<AppException, List<FlowRun>> =
        either {
            flowRunDao.getAll()
                .filter { item -> item.userUid == userUid }
        }

    fun getByProjectUid(projectUid: Uid): Either<AppException, List<FlowRun>> =
        either {
            val flowUids = flowDao.getAll()
                .filter { flow -> flow.projectUid == projectUid }
                .map { flow -> flow.uid }
                .toSet()

            val flowRuns = flowRunDao.getAll()
                .filter { flowRun -> flowRun.flowUid in flowUids }

            flowRuns
        }

    fun putReportContent(
        flowRunUid: Uid,
        flowUid: Uid,
        content: String
    ): Either<AppException, FsPath> =
        either {
            val flow = flowDao.findByUid(flowUid)
                ?: raise(EntityNotFoundByUidException(Flow::class, flowUid))

            val project = projectDao.findByUid(flow.projectUid)
                ?: raise(EntityNotFoundByUidException(Project::class, flow.projectUid))

            val path = FsPath("${project.name}/$flowRunUid.text")

            fileStorage.putContent(
                destination = StorageDestination.REPORTS,
                path = path,
                content = content
            ).bind()

            path
        }

    fun getReportContent(flowRunUid: Uid): Either<AppException, String> =
        either {
            val flowRun = flowRunDao.findByUid(flowRunUid)
                ?: raise(EntityNotFoundByUidException(FlowRun::class, flowRunUid))

            fileStorage.getContent(
                destination = StorageDestination.REPORTS,
                path = flowRun.reportPath
            ).bind()
        }

    fun add(flowRun: FlowRun): Either<AppException, FlowRun> =
        either {
            flowRunDao.add(flowRun)

            flowRunDao.findByUid(flowRun.uid)
                ?: raise(EntityNotFoundByUidException(FlowRun::class, flowRun.uid))
        }
}