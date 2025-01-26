package com.github.aivanovski.testswithme.flow.runner.report.model.exception

import com.github.aivanovski.testswithme.entity.exception.ParsingException
import com.github.aivanovski.testswithme.flow.yaml.model.TextLine

open class ReportParsingException(
    message: String
) : ParsingException(message = message)

class EmptyParameterException(
    line: TextLine
) : ReportParsingException(
    message = "Empty parameter exception at line ${line.number}: ${line.text}"
)

class InvalidLineFormatException(
    line: TextLine
) : ReportParsingException(
    message = "Invalid report format at line ${line.number}: ${line.text}"
)

class InvalidParserStateException(
    line: TextLine? = null,
    message: String? = null
) : ReportParsingException(
    message = when {
        line != null -> "Invalid parser state at line ${line.number}: ${line.text}"
        message != null -> message
        else -> "Invalid parser state"
    }
)

class InvalidFlowNameException(
    flowName: String?
) : ReportParsingException(message = "Invalid flow name: $flowName")