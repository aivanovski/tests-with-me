package com.github.aivanovski.testswithme.domain.validation

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.exception.ValidationException
import java.util.regex.Pattern

class ValidateEmailUseCase {

    fun validateEmail(email: String): Either<ValidationException, Unit> =
        either {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                raise(ValidationException("Invalid email address"))
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