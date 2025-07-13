package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.FlowDao
import com.github.aivanovski.testswithme.web.data.database.dao.ProjectDao
import com.github.aivanovski.testswithme.web.data.database.dao.TextChunkDao
import com.github.aivanovski.testswithme.web.entity.Flow
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.TextChunk.DbFields.CHUNK_SIZE_IN_BYTES
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.DeletedEntityAccessException
import com.github.aivanovski.testswithme.web.entity.exception.EntityNotFoundByUidException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidAccessException
import com.github.aivanovski.testswithme.web.entity.exception.InvalidEntityIdException
import com.github.aivanovski.testswithme.web.extensions.splitIntoChunks

class FlowRepository(
    private val flowDao: FlowDao,
    private val projectDao: ProjectDao,
    private val textChunkDao: TextChunkDao
) {

    fun getFlowContent(uid: Uid): Either<AppException, String> =
        either {
            textChunkDao.getByEntityUid(uid)
                .joinToString(
                    separator = "",
                    transform = { chunk -> chunk.content }
                )
        }

    fun findByUid(uid: Uid): Either<AppException, Flow?> =
        either {
            val flow = flowDao.findByUid(uid)

            if (flow?.isDeleted == true) {
                raise(DeletedEntityAccessException(Flow::class))
            }

            flow
        }

    fun getByUid(uid: Uid): Either<AppException, Flow> =
        either {
            val flow = findByUid(uid).bind()
                ?: raise(EntityNotFoundByUidException(Flow::class, uid))

            if (flow.isDeleted) {
                raise(DeletedEntityAccessException(Flow::class))
            }

            flow
        }

    fun getFlowsByProjectUid(projectUid: Uid): Either<AppException, List<Flow>> =
        either {
            flowDao.getAll()
                .filterNotDeleted()
                .filter { flow -> flow.projectUid == projectUid }
        }

    fun getFlowsByUserUid(userUid: Uid): Either<AppException, List<Flow>> =
        either {
            val projectUids = projectDao.getByUserUid(userUid)
                .map { project -> project.uid }
                .toSet()

            val flows = flowDao.getAll()
                .filterNotDeleted()
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
                .filterNotDeleted()
                .filter { flow -> flow.projectUid == projectUid }
                .filter { flow -> flow.groupUid == groupUid }

            flows
        }

    fun add(
        flow: Flow,
        content: String
    ): Either<AppException, Flow> =
        either {
            val chunks = content.splitIntoChunks(
                entityUid = flow.uid,
                chunkSize = CHUNK_SIZE_IN_BYTES
            )
            flowDao.add(flow)
            textChunkDao.add(chunks)

            getByUid(flow.uid).bind()
        }

    fun update(flow: Flow): Either<AppException, Flow> =
        either {
            if (flow.id == 0L) {
                raise(InvalidEntityIdException(Flow::class))
            }

            flowDao.update(flow)
        }

    fun update(
        flow: Flow,
        content: String
    ): Either<AppException, Flow> =
        either {
            if (flow.id == 0L) {
                raise(InvalidEntityIdException(Flow::class))
            }

            val oldChunks = textChunkDao.getByEntityUid(flow.uid)
            textChunkDao.delete(
                ids = oldChunks.map { chunk -> chunk.id }
            )

            val newChunks = content.splitIntoChunks(
                entityUid = flow.uid,
                chunkSize = CHUNK_SIZE_IN_BYTES
            )

            textChunkDao.add(newChunks)
            flowDao.update(flow)
        }

    private fun List<Flow>.filterNotDeleted(): List<Flow> = this.filter { flow -> !flow.isDeleted }
}