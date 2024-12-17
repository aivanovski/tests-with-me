package com.github.aivanovski.testswithme.android.data.settings.encryption.entity

class BiometricData(
    val initVector: ByteArray,
    val encryptedData: ByteArray
)