package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.db.dao.FlowEntryDao
import com.github.aivanovski.testswithme.android.data.db.dao.GroupEntryDao
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import com.github.aivanovski.testswithme.web.api.response.PostGroupResponse
import com.github.aivanovski.testswithme.web.api.response.UpdateGroupResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class GroupRepository(
    private val groupDao: GroupEntryDao,
    private val flowDao: FlowEntryDao,
    private val api: ApiClient,
    private val authRepository: AuthRepository
) {

    suspend fun createGroup(request: PostGroupRequest): Either<AppException, PostGroupResponse> =
        either {
            api.postGroup(request).bind()
        }

    suspend fun updateGroup(
        groupUid: String,
        request: UpdateGroupRequest
    ): Either<AppException, UpdateGroupResponse> =
        either {
            api.putGroup(groupUid, request).bind()
        }

    fun getGroupsFlow(): Flow<Either<AppException, List<GroupEntry>>> =
        flow {
            val localGroups = groupDao.getAll()

            if (!authRepository.isUserLoggedIn()) {
                emit(localGroups.right())
                return@flow
            }

            if (localGroups.isNotEmpty()) {
                emit(localGroups.right())
            }

            val getRemoteGroupsResult = api.getGroups()
            if (getRemoteGroupsResult.isLeft()) {
                if (localGroups.isEmpty()) {
                    emit(getRemoteGroupsResult)
                }
                return@flow
            }

            val remoteGroups = getRemoteGroupsResult.unwrap()

            mergeEntities(
                localEntities = localGroups,
                remoteEntities = remoteGroups,
                entityToUidMapper = { group -> group.uid },
                onInsert = { group -> groupDao.insert(group) },
                onUpdate = { local, remote -> groupDao.update(remote.copy(id = local.id)) },
                onDelete = { group -> groupDao.removeByUid(group.uid) }
            )

            emit(Either.Right(groupDao.getAll()))
        }
            .distinctUntilChanged()

    suspend fun getGroups(): Either<AppException, List<GroupEntry>> =
        either {
            if (!authRepository.isUserLoggedIn()) {
                return@either groupDao.getAll()
            }

            mergeEntities(
                localEntities = groupDao.getAll(),
                remoteEntities = api.getGroups().bind(),
                entityToUidMapper = { group -> group.uid },
                onInsert = { group -> groupDao.insert(group) },
                onUpdate = { local, remote -> groupDao.update(remote.copy(id = local.id)) },
                onDelete = { group -> groupDao.removeByUid(group.uid) }
            )

            groupDao.getAll()
        }

    suspend fun getGroupsByProjectUid(projectUid: String): Either<AppException, List<GroupEntry>> =
        either {
            getGroups().bind()
                .filter { group -> group.projectUid == projectUid }
        }

    suspend fun getGroupByUid(uid: String): Either<AppException, GroupEntry> =
        either {
            getGroups().bind()
                .firstOrNull { group -> group.uid == uid }
                ?: raise(FailedToFindEntityByUidException(GroupEntry::class, uid))
        }

    suspend fun removeByUid(uid: String): Either<AppException, Unit> =
        either {
            val response = api.deleteGroup(groupUid = uid).bind()

            val removedGroupUids = response.modifiedGroupIds
            val removedFlowUids = response.modifiedFlowIds

            for (flowUid in removedFlowUids) {
                flowDao.removeByUid(flowUid)
            }

            for (groupUid in removedGroupUids) {
                groupDao.removeByUid(groupUid)
            }

            groupDao.removeByUid(uid)
        }

    fun clear() {
        groupDao.removeAll()
    }
}