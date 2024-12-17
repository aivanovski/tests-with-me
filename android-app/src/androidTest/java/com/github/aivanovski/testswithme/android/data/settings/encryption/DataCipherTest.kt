package com.github.aivanovski.testswithme.android.data.settings.encryption

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aivanovski.testswithme.android.data.settings.encryption.DataCipherConstants.ANDROID_KEY_STORE
import com.github.aivanovski.testswithme.android.data.settings.encryption.DataCipherConstants.KEY_ALIAS
import com.github.aivanovski.testswithme.android.data.settings.encryption.entity.CipherTransformation
import com.github.aivanovski.testswithme.android.data.settings.encryption.keyprovider.KeyStoreSecretKeyProvider
import com.github.aivanovski.testswithme.android.data.settings.encryption.keyprovider.SecretKeyProvider
import com.google.common.truth.Truth.assertThat
import java.security.KeyStore
import java.security.KeyStoreException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataCipherTest {

    private lateinit var context: Context
    private lateinit var keyProvider: SecretKeyProvider
    private lateinit var cipher: DataCipherImpl

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        keyProvider = KeyStoreSecretKeyProvider()
        cipher = DataCipherImpl(keyProvider, CipherTransformation.AES_CBC_PKCS7)

        keyProvider.removeKeyIfNeed()
    }

    @After
    fun tearDown() {
        keyProvider.removeKeyIfNeed()
    }

    @Test
    fun shouldGenerateNewWhenNotExist() {
        // arrange
        assertThat(keyProvider.isKeyExist()).isFalse()

        // act
        cipher.encode(TEXT)

        // assert
        assertThat(keyProvider.isKeyExist()).isTrue()
    }

    @Test
    fun shouldLoadExistingKey() {
        // arrange
        val encodedText =
            DataCipherImpl(keyProvider, CipherTransformation.AES_CBC_PKCS7).encode(TEXT)
        assertThat(keyProvider.isKeyExist()).isTrue()
        requireNotNull(encodedText)

        // act
        val decodedText = cipher.decode(encodedText)

        // assert
        assertThat(decodedText).isEqualTo(TEXT)
    }

    @Test
    fun shouldEncodeText() {
        // arrange
        assertThat(keyProvider.isKeyExist()).isFalse()

        // act
        val encodedText = cipher.encode(TEXT)

        // assert
        assertThat(encodedText).isNotNull()
        assertThat(encodedText).isNotEqualTo(TEXT)
    }

    @Test
    fun shouldDecodeText() {
        // arrange
        assertThat(keyProvider.isKeyExist()).isFalse()

        // act
        val text = cipher.encodeAndDecode(TEXT)

        // assert
        assertThat(text).isEqualTo(TEXT)
    }

    private fun SecretKeyProvider.isKeyExist(): Boolean {
        return when (this) {
            is KeyStoreSecretKeyProvider -> {
                try {
                    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
                    keyStore.load(null)
                    keyStore.getEntry(KEY_ALIAS, null) != null
                } catch (e: KeyStoreException) {
                    e.printStackTrace()
                    false
                }
            }

            else -> throw IllegalStateException()
        }
    }

    private fun SecretKeyProvider.removeKeyIfNeed() {
        when (this) {
            is KeyStoreSecretKeyProvider -> {
                if (isKeyExist()) {
                    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
                    keyStore.load(null)
                    keyStore.deleteEntry(KEY_ALIAS)
                }
            }

            else -> throw IllegalStateException()
        }
    }

    private fun DataCipher.encodeAndDecode(text: String): String? {
        val encoded = encode(text) ?: return null
        return decode(encoded)
    }

    companion object {
        private const val TEXT = "plain-text"
    }
}