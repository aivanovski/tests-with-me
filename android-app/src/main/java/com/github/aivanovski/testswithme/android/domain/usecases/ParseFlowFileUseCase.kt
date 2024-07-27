package com.github.aivanovski.testswithme.android.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.entity.exception.ParsingException
import com.github.aivanovski.testswithme.android.utils.Base64Utils
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.flow.yaml.YamlParser

class ParseFlowFileUseCase {

    fun parseBase64File(base64content: String): Either<ParsingException, YamlFlow> =
        either {
            val decodedContent = Base64Utils.decode(base64content)
                ?: raise(ParsingException("Invalid bas64 string"))

            parse(decodedContent).bind()
        }

    private fun parse(content: String): Either<ParsingException, YamlFlow> =
        either {
            val flow = YamlParser().parse(content)
                .mapLeft { exception -> ParsingException(cause = exception) }
                .bind()

            flow
        }
}