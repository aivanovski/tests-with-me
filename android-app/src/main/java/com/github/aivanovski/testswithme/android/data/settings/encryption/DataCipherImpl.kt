package com.github.aivanovski.testswithme.android.data.settings.encryption

import android.os.Build
import com.github.aivanovski.testswithme.android.data.settings.encryption.DataCipherConstants.ANDROID_KEY_STORE
import com.github.aivanovski.testswithme.android.data.settings.encryption.entity.Base64SecretData
import com.github.aivanovski.testswithme.android.data.settings.encryption.entity.CipherTransformation
import com.github.aivanovski.testswithme.android.data.settings.encryption.entity.SecretData
import com.github.aivanovski.testswithme.android.data.settings.encryption.entity.toBase64SecretData
import com.github.aivanovski.testswithme.android.data.settings.encryption.entity.toSecretData
import com.github.aivanovski.testswithme.android.data.settings.encryption.keyprovider.SecretKeyProvider
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import timber.log.Timber

class DataCipherImpl(
    private val secretKeyProvider: SecretKeyProvider,
    private val transformation: CipherTransformation
) : DataCipher {

    private var key: SecretKey? = null

    override fun encode(data: String): String? {
        var result: SecretData? = null

        val cipher = initCipherForEncode()
        if (cipher != null) {
            val initVector = cipher.iv

            try {
                val encodedBytes = cipher.doFinal(data.toByteArray())
                result = SecretData(initVector, encodedBytes)
            } catch (e: GeneralSecurityException) {
                Timber.d(e)
            }
        }

        return result?.toBase64SecretData()?.toString()
    }

    override fun decode(data: String): String? {
        val secreteData = Base64SecretData.parse(data)
            ?.toSecretData()
            ?: return null

        return decode(secreteData)
    }

    private fun decode(data: SecretData): String? {
        var result: String? = null

        val cipher = initCipherForDecode(data.initVector)
        if (cipher != null) {
            try {
                val decodedBytes = cipher.doFinal(data.encryptedData)
                if (decodedBytes != null) {
                    result = String(decodedBytes, Charsets.UTF_8)
                }
            } catch (e: GeneralSecurityException) {
                Timber.d(e)
            }
        }

        return result
    }

    private fun initCipherForEncode(): Cipher? {
        var cipher: Cipher? = null

        val key = getOrCreateSecretKey()
        if (key != null) {
            cipher = initCipher(Cipher.ENCRYPT_MODE, key, null)
        }

        return cipher
    }

    private fun initCipherForDecode(initVector: ByteArray): Cipher? {
        var cipher: Cipher? = null

        val key = getOrLoadSecretKey()
        if (key != null) {
            cipher = initCipher(Cipher.DECRYPT_MODE, key, initVector)
        }

        return cipher
    }

    private fun getOrLoadSecretKey(): SecretKey? {
        if (key != null) return key

        key = secretKeyProvider.getSecretKey(isCreateIfNeed = false)

        return key
    }

    private fun getOrCreateSecretKey(): SecretKey? {
        if (key != null) return key

        key = secretKeyProvider.getSecretKey(isCreateIfNeed = true)

        return key
    }

    private fun initCipher(
        mode: Int,
        key: SecretKey,
        initVector: ByteArray?
    ): Cipher? {
        var result: Cipher? = null

        try {
            val cipher = Cipher.getInstance(transformation.value)

            if (initVector != null) {
                cipher.init(mode, key, IvParameterSpec(initVector))
            } else {
                cipher.init(mode, key)
            }

            result = cipher
        } catch (e: InvalidKeyException) {
            Timber.d(e)
        }

        return result
    }

    companion object {

        fun isAndroidKeyStoreCipherAllowed(): Boolean {
            return Build.VERSION.SDK_INT >= 23 &&
                KeyStore.getInstance(ANDROID_KEY_STORE) != null
        }
    }
}