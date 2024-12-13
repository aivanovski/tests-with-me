package com.github.aivanovski.testswithme.android.data.settings.encryption.keyprovider

import javax.crypto.SecretKey

interface SecretKeyProvider {
    fun getSecretKey(isCreateIfNeed: Boolean): SecretKey?
}