package com.github.aivanovski.testswithme.android.data.settings.encryption.entity

import com.github.aivanovski.testswithme.utils.Base64Utils

fun Base64SecretData.toSecretData(): SecretData? {
    val initVector = Base64Utils.decodeToBytes(initVector).getOrNull()
        ?: return null

    val data = Base64Utils.decodeToBytes(encryptedText).getOrNull()
        ?: return null

    return SecretData(
        initVector = initVector,
        encryptedData = data
    )
}

fun SecretData.toBase64SecretData(): Base64SecretData {
    return Base64SecretData(
        initVector = Base64Utils.encode(initVector),
        encryptedText = Base64Utils.encode(encryptedData)
    )
}

fun SecretData.toBiometricData(): BiometricData =
    BiometricData(
        initVector = initVector,
        encryptedData = encryptedData
    )

fun BiometricData.toSecretData(): SecretData =
    SecretData(
        initVector = initVector,
        encryptedData = encryptedData
    )