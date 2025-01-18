package com.github.aivanovski.testswithme.flow.yaml.model.exception

import com.github.aivanovski.testswithme.entity.exception.ParsingException
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.flow.yaml.model.TextLine

open class YamlParsingException(
    message: String
) : ParsingException(message = message)

class InvalidIndentationException(
    line: TextLine
) : YamlParsingException("Invalid indentation at line ${line.number}: ${line.text}")

class InvalidLineFormatException(
    line: TextLine
) : YamlParsingException("Invalid YAML format at line ${line.number}: ${line.text}")

class InvalidElementDataException(
    type: YamlParser.AnchorType,
    line: TextLine
) : YamlParsingException("Invalid '${type.key}' data at line ${line.number}: ${line.text}")

class MissingCharacterException(
    character: Char,
    line: TextLine
) : YamlParsingException("Missing character '$character' at line ${line.number}: ${line.text}")

class InvalidKeyException(
    line: TextLine
) : YamlParsingException("Invalid key at line ${line.number}: ${line.text}")