package com.github.aivanovski.testswithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.web.entity.SslKeyStore
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.AppIoException
import com.github.aivanovski.testswithme.web.entity.exception.ParsingException
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.util.Properties

class GetSslKeyStoreUseCase(
    private val fileSystemProvider: FileSystemProvider
) {

    fun getKeyStore(): Either<AppException, SslKeyStore> =
        either {
            val devKeyStoreBytes = fileSystemProvider.readBytes(DEV_KEY_STORE_PATH)
                .mapLeft { exception -> AppIoException(cause = exception) }
                .bind()

            val properties = fileSystemProvider.readBytes(DEV_PROPERTIES_PATH)
                .mapLeft { exception -> AppIoException(cause = exception) }
                .map { bytes ->
                    Properties()
                        .apply {
                            load(ByteArrayInputStream(bytes))
                        }
                }
                .bind()

            val alias = properties.getProperty(PROPERTY_ALIAS)
            val password = properties.getProperty(PROPERTY_PASSWORD)

            if (alias.isBlank()) {
                raise(
                    ParsingException(
                        message = "Invalid keystore property: %s".format(PROPERTY_ALIAS)
                    )
                )
            }

            if (password.isBlank()) {
                raise(
                    ParsingException(
                        message = "Invalid keystore property: %s".format(PROPERTY_PASSWORD)
                    )
                )
            }

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(ByteArrayInputStream(devKeyStoreBytes), password.toCharArray())

            SslKeyStore(
                keyStore = keyStore,
                alias = alias,
                password = password
            )
        }

    companion object {
        private const val DEV_KEY_STORE_PATH = "keys/dev-keystore.jks"
        private const val DEV_PROPERTIES_PATH = "keys/dev-keystore.properties"

        private const val PROPERTY_ALIAS = "alias"
        private const val PROPERTY_PASSWORD = "password"
    }
}