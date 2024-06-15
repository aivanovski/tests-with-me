package com.github.aivanovski.testwithme.android.domain.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.StepEntry
import com.github.aivanovski.testwithme.android.entity.FlowSourceType
import com.github.aivanovski.testwithme.android.entity.FlowWithSteps
import com.github.aivanovski.testwithme.android.entity.StepVerificationType
import com.github.aivanovski.testwithme.android.entity.exception.ParsingException
import com.github.aivanovski.testwithme.android.utils.Base64Utils
import com.github.aivanovski.testwithme.flow.yaml.YamlParser

class ParseFlowFileUseCase {

    fun parseBase64File(
        base64content: String
    ): Either<ParsingException, FlowWithSteps> = either {
        val decodedContent = Base64Utils.decode(base64content)
            ?: raise(ParsingException("Invalid bas64 string"))

        parse(decodedContent).bind()
    }

    private fun parse(
        content: String,
    ): Either<ParsingException, FlowWithSteps> = either {
        val flow = YamlParser().parse(content)
            .mapLeft { exception -> ParsingException(cause = exception) }
            .bind()

        val flowUid = flow.name
        val convertedSteps = flow.steps

        val steps = mutableListOf<StepEntry>()
        for (stepIdx in convertedSteps.indices) {
            val step = convertedSteps[stepIdx]
            val nextStep = convertedSteps.getOrNull(stepIdx + 1)
            val stepUid = "$flowUid:$stepIdx"

            val nextUid = if (nextStep != null) {
                "$flowUid:${stepIdx + 1}"
            } else {
                null
            }

            steps.add(
                StepEntry(
                    id = null,
                    uid = stepUid,
                    index = stepIdx,
                    flowUid = flowUid,
                    nextUid = nextUid,
                    command = step,
                    stepVerificationType = StepVerificationType.LOCAL
                )
            )
        }

        return Either.Right(
            FlowWithSteps(
                entry = FlowEntry(
                    id = null,
                    uid = flowUid,
                    name = flowUid,
                    sourceType = FlowSourceType.REMOTE
                ),
                steps = steps
            )
        )
    }
}