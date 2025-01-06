package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import arrow.core.right
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.data.db.dao.UserEntryDao
import com.github.aivanovski.testswithme.android.entity.db.UserEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.extensions.unwrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val userDao: UserEntryDao,
    private val api: ApiClient,
    private val authRepository: AuthRepository
) {

    fun getUsersFlow(): Flow<Either<AppException, List<UserEntry>>> =
        flow {
            val localUsers = userDao.getAll()

            if (!authRepository.isUserLoggedIn()) {
                emit(localUsers.right())
                return@flow
            }

            if (localUsers.isNotEmpty()) {
                emit(localUsers.right())
            }

            val getUsersResult = api.getUsers()
            if (getUsersResult.isLeft()) {
                if (localUsers.isEmpty()) {
                    emit(localUsers.right())
                }
                return@flow
            }

            val remoteUsers = getUsersResult.unwrap()

            mergeEntities(
                localEntities = localUsers,
                remoteEntities = remoteUsers,
                entityToUidMapper = { user -> user.uid },
                onInsert = { user -> userDao.insert(user) },
                onUpdate = { local, remote -> userDao.update(remote.copy(id = local.id)) },
                onDelete = { user -> userDao.removeByUid(user.uid) }
            )

            emit(userDao.getAll().right())
        }

    suspend fun getUsers(): Either<AppException, List<UserEntry>> {
        return api.getUsers()
    }
}