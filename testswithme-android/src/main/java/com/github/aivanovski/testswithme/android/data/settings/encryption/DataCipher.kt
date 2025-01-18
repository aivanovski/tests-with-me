package com.github.aivanovski.testswithme.android.data.settings.encryption

interface DataCipher {
    fun encode(data: String): String?
    fun decode(data: String): String?
}