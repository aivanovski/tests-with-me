package com.github.aivanovski.testswithme.android.domain.usecases

import com.github.aivanovski.testswithme.android.data.settings.Settings

class IsUserLoggedInUseCase(
    private val settings: Settings
) {

    fun isLoggedIn(): Boolean = settings.authToken != null
}