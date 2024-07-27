package com.github.aivanovski.testwithme.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.api.ApiClient
import com.github.aivanovski.testwithme.android.entity.Group
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FailedToFindEntityByUidException

class GroupRepository(
    private val api: ApiClient
) {

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