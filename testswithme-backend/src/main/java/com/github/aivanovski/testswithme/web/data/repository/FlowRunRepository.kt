package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.FlowDao
import com.github.aivanovski.testswithme.web.data.database.dao.FlowRunDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.data.database.dao.TextChunkDao
import com.github.aivanovski.testswithme.web.entity.FlowRun
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidEntityIdException
import com.github.aivanovski.testswithme.web.extensions.splitIntoChunks

class FlowRunRepository(
    private val flowRunDao: FlowRunDao,
    private val flowDao: FlowDao,
    private val projectDao: ProjectDao,
    private val textChunkDao: TextChunkDao
) {

    fun getAll(): Either<AppException, List<FlowRun>> {
        return Either.Right(flowRunDao.getAll())
    }

    fun findByUid(uid: Uid): Either<AppException, FlowRun?> =
        either {
            flowRunDao.findByUid(uid)
        }

    fun getByUid(uid: Uid): Either<AppException, FlowRun> =
        either {
            findByUid(uid).bind()
                ?: raise(EntityNotFoundByUidException(FlowRun::class, uid))
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

    fun getReportContent(flowRunUid: Uid): Either<AppException, String> =
        either {
            textChunkDao.getByEntityUid(flowRunUid)
                .joinToString(
                    separator = "",
                    transform = { chunk -> chunk.content }
                )
        }

    fun add(flowRun: FlowRun, report: String): Either<AppException, FlowRun> =
        either {
            val chunks = report.splitIntoChunks(entityUid = flowRun.uid)
            flowRunDao.add(flowRun)
            textChunkDao.add(chunks)

            getByUid(flowRun.uid).bind()
        }

    fun update(flowRun: FlowRun): Either<AppException, FlowRun> =
        either {
            if (flowRun.id == 0L) {
                raise(InvalidEntityIdException(FlowRun::class))
            }

            flowRunDao.update(flowRun)
        }
}