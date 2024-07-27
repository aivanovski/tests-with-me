package com.github.aivanovski.testswithme.android.data.repository

import arrow.core.Either
import com.github.aivanovski.testswithme.android.data.api.ApiClient
import com.github.aivanovski.testswithme.android.entity.User
import com.github.aivanovski.testswithme.android.entity.exception.AppException

class UserRepository(
    private val api: ApiClient
) {

    suspend fun getUsers(): Either<AppException, List<User>> {
        return api.getUsers()
    }
}