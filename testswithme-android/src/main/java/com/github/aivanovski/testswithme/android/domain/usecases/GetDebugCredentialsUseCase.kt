package com.github.aivanovski.testswithme.android.domain.usecases

import com.github.aivanovski.testswithme.android.BuildConfig

class GetDebugCredentialsUseCase {

    fun getDebugCredentials(): List<Pair<String, String>> {
        val users = BuildConfig.DEBUG_USERS?.toList() ?: emptyList()
        val passwords = BuildConfig.DEBUG_PASSWORDS?.toList() ?: emptyList()

        return users.zip(passwords)
    }
}