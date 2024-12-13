package com.github.aivanovski.testswithme.android.data.settings.encryption

import android.content.Context
import com.github.aivanovski.testswithme.android.data.settings.encryption.entity.CipherTransformation
import com.github.aivanovski.testswithme.android.data.settings.encryption.keyprovider.KeyStoreSecretKeyProvider

class DataCipherProviderImpl(
    private val context: Context
) : DataCipherProvider {

    private val dataCipher: DataCipher by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        instantiateDataCipher()
    }

    override fun getCipher(): DataCipher {
        return dataCipher
    }

    private fun instantiateDataCipher(): DataCipher {
        return DataCipherImpl(KeyStoreSecretKeyProvider(), CipherTransformation.AES_CBC_PKCS7)
    }
}