package com.github.aivanovski.testswithme.android.data.settings.encryption

interface DataCipherProvider {
    fun getCipher(): DataCipher
}