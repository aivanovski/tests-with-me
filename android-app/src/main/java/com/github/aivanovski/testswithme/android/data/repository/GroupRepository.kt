package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.db.dao.GroupEntryDao
import com.github.aivanovski.testswithme.android.domain.usecases.IsUserLoggedInUseCase
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import com.github.aivanovski.testswithme.web.api.response.PostGroupResponse
import com.github.aivanovski.testswithme.web.api.response.UpdateGroupResponse

class GroupRepository(
    private val groupDao: GroupEntryDao,
    private val api: ApiClient,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
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

    suspend fun getGroups(): Either<AppException, List<GroupEntry>> =
        either {
            if (!isUserLoggedInUseCase.isLoggedIn()) {
                return@either groupDao.getAll()
            }

            val remoteGroups = api.getGroups().bind()
            val uidToLocalGroupMap = groupDao.getAll()
                .associateBy { group -> group.uid }

            for (remote in remoteGroups) {
                val local = uidToLocalGroupMap[remote.uid]
                if (local != null) {
                    groupDao.update(remote.copy(id = local.id))
                } else {
                    groupDao.insert(remote)
                }
            }

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
}