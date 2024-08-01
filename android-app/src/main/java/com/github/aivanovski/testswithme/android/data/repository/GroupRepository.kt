package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.FailedToFindEntityByUidException
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import com.github.aivanovski.testswithme.web.api.response.PostGroupResponse
import com.github.aivanovski.testswithme.web.api.response.UpdateGroupResponse

class GroupRepository(
    private val api: ApiClient
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

    suspend fun getGroups(): Either<AppException, List<Group>> =
        either {
            api.getGroups().bind()
        }

    suspend fun getGroupsByProjectUid(projectUid: String): Either<AppException, List<Group>> =
        either {
            getGroups()
                .bind()
                .filter { group -> group.projectUid == projectUid }
        }

    suspend fun getGroupByUid(uid: String): Either<AppException, Group> =
        either {
            getGroups()
                .bind()
                .firstOrNull { group -> group.uid == uid }
                ?: raise(FailedToFindEntityByUidException(Group::class, uid))
        }
}