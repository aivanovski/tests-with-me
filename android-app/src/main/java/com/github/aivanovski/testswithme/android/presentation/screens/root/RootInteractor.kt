package com.github.aivanovski.testswithme.android.presentation.screens.root

import com.github.aivanovski.testswithme.android.data.repository.AuthRepository
import com.github.aivanovski.testswithme.android.domain.usecases.ClearDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RootInteractor(
    private val authRepository: AuthRepository,
    private val clearDataUseCase: ClearDataUseCase
) {

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    suspend fun logout() =
        withContext(Dispatchers.IO) {
            clearDataUseCase.clearUserData()
        }
}