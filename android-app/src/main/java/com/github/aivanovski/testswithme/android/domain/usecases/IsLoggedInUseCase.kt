package com.github.aivanovski.testswithme.android.domain.usecases

import com.github.aivanovski.testswithme.android.data.settings.Settings

class IsLoggedInUseCase(
    private val settings: Settings
) {

    fun isLoggedIn(): Boolean = settings.authToken != null
}