package com.github.aivanovski.testwithme.web.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.web.entity.exception.ValidationException
import java.util.regex.Pattern

class ValidateEmailUseCase {

    fun validateEmail(email: String): Either<ValidationException, Unit> =
        either {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                raise(ValidationException("Failed to validate email: $email"))
            }
        }

    companion object {
        private val EMAIL_PATTERN =
            Pattern.compile(
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                    "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
            )
    }
}