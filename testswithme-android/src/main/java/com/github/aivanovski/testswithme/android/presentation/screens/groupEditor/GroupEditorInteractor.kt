package com.github.aivanovski.testswithme.android.presentation.screens.groupEditor

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.repository.GroupRepository
import com.github.aivanovski.testswithme.android.entity.db.GroupEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.web.api.request.PostGroupRequest
import com.github.aivanovski.testswithme.web.api.request.UpdateGroupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupEditorInteractor(
    private val groupRepository: GroupRepository
) {

    suspend fun loadGroup(groupUid: String): Either<AppException, GroupEntry> =
        withContext(Dispatchers.IO) {
            either {
                groupRepository.getGroupByUid(groupUid).bind()
            }
        }

    suspend fun createGroup(request: PostGroupRequest): Either<AppException, GroupEntry> =
        withContext(Dispatchers.IO) {
            either {
                val response = groupRepository.createGroup(request).bind()
                groupRepository.getGroupByUid(response.id).bind()
            }
        }

    suspend fun updateGroup(
        groupUid: String,
        request: UpdateGroupRequest
    ): Either<AppException, GroupEntry> =
        withContext(Dispatchers.IO) {
            either {
                val response = groupRepository.updateGroup(groupUid, request).bind()
                groupRepository.getGroupByUid(response.group.id).bind()
            }
        }
}