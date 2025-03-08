package com.github.aivanovski.testswithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.web.entity.JwtData
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.AppIoException
import com.github.aivanovski.testswithme.web.extensions.asRelativePath
import java.io.ByteArrayInputStream
import java.util.Properties

class GetJwtDataUseCase(
    private val fileSystemProvider: FileSystemProvider
) {

    fun getJwtData(): Either<AppException, JwtData> =
        either {
            val properties = fileSystemProvider.readBytes(JWT_PROPERTIES_PATH.asRelativePath())
                .mapLeft { exception -> AppIoException(cause = exception) }
                .map { bytes ->
                    Properties()
                        .apply {
                            load(ByteArrayInputStream(bytes))
                        }
                }
                .bind()

            val secret = properties.getProperty("secret").trim()
            if (secret.isEmpty()) {
                raise(AppException("Empty jwt secret"))
            }

            JwtData(
                secret = secret,
                issuer = "https://testswithme.org",
                audience = "http://testswithme.org",
                realm = "TestWithMe"
            )
        }

    companion object {
        private const val JWT_PROPERTIES_PATH = "data/jwt.properties"
    }
}