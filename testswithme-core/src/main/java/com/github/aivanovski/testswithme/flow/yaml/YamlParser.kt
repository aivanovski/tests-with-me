package com.github.aivanovski.testswithme.flow.yaml

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.ConditionType
import com.github.aivanovski.testswithme.entity.Duration
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.FlowStepPrecondition
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.extensions.toIntSafely
import com.github.aivanovski.testswithme.flow.yaml.extensions.isBlank
import com.github.aivanovski.testswithme.flow.yaml.extensions.keySubstring
import com.github.aivanovski.testswithme.flow.yaml.extensions.valueSubstring
import com.github.aivanovski.testswithme.flow.yaml.model.TextLine
import com.github.aivanovski.testswithme.flow.yaml.model.TextLineRange
import com.github.aivanovski.testswithme.flow.yaml.model.exception.InvalidElementDataException
import com.github.aivanovski.testswithme.flow.yaml.model.exception.InvalidIndentationException
import com.github.aivanovski.testswithme.flow.yaml.model.exception.InvalidLineFormatException
import com.github.aivanovski.testswithme.flow.yaml.model.exception.YamlParsingException
import com.github.aivanovski.testswithme.utils.StringUtils
import java.util.Deque
import java.util.LinkedList

class YamlParser {

    fun parse(content: String): Either<YamlParsingException, YamlFlow> =
        either {
            val steps = mutableListOf<FlowStep>()
            val stepLines = mutableListOf<TextLineRange>()

            val lines = LinkedList<TextLine>()
                .apply {
                    addAll(content.transformToTextLines())
                }

            var nameNode: AnchorNode? = null
            var projectNode: AnchorNode? = null
            var groupNode: AnchorNode? = null

            while (lines.isNotEmpty()) {
                val line = lines.removeFirst()
                if (line.isBlank()) {
                    continue
                }

                if (!line.isAnchorLine()) {
                    raise(InvalidLineFormatException(line))
                }

                val anchor = parseAnchorNode(line).bind()

                when (anchor.type) {
                    AnchorType.NAME -> nameNode = anchor
                    AnchorType.PROJECT -> projectNode = anchor
                    AnchorType.GROUP -> groupNode = anchor
                    else -> {
                        val step = parseStep(line, lines).bind()

                        steps.add(step.step)
                        stepLines.add(step.lines)
                    }
                }
            }

            val name = nameNode?.value?.getStringValue()?.getOrNull()
                ?: StringUtils.EMPTY

            val project = projectNode?.value?.getStringValue()?.getOrNull()
            val group = groupNode?.value?.getStringValue()?.getOrNull()

            YamlFlow(
                name = name,
                project = project,
                group = group,
                steps = steps,
                stepLines = stepLines
            )
        }

    private fun String.transformToTextLines(): List<TextLine> {
        return this.lines()
            .map { line ->
                line.substringBefore("#")
            }
            .withIndex()
            .map { (index, line) -> TextLine(index + 1, line) }
    }

    private fun parseStep(
        anchorLine: TextLine,
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, StepWithLines> =
        either {
            val anchorType = parseAnchorNode(anchorLine).bind().type

            when (anchorType) {
                AnchorType.TAP_ON -> parseTap(anchorLine, allLines).bind()
                AnchorType.LONG_TAP_ON -> parseTap(anchorLine, allLines).bind()
                AnchorType.LAUNCH -> parseLaunch(anchorLine).bind()
                AnchorType.SEND_BROADCAST -> parseSendBroadcast(anchorLine, allLines).bind()
                AnchorType.ASSERT_VISIBLE -> parseAssert(anchorLine, allLines).bind()
                AnchorType.ASSERT_NOT_VISIBLE -> parseAssert(anchorLine, allLines).bind()
                AnchorType.RUN_FLOW -> parseRunFlow(anchorLine, allLines).bind()
                AnchorType.INPUT_TEXT -> parseInputText(anchorLine, allLines).bind()
                AnchorType.WAIT_UNTIL -> parseWaitUntil(anchorLine, allLines).bind()
                AnchorType.PRESS_KEY -> parsePressKey(anchorLine).bind()
                else -> raise(InvalidLineFormatException(anchorLine))
            }
        }

    private fun parseLaunch(anchorLine: TextLine): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val packageName = anchor.value.getStringValue().bind()
            if (packageName.isBlank()) {
                raise(InvalidElementDataException(AnchorType.LAUNCH, anchorLine))
            }

            StepWithLines(
                step = FlowStep.Launch(packageName),
                lines = mergeLines(anchorLine)
            )
        }

    private fun parsePressKey(anchorLine: TextLine): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val key = anchor.value.getStringValue().bind()

            val keyCode = KEY_CODES[key]
                ?: raise(InvalidElementDataException(AnchorType.PRESS_KEY, anchorLine))

            StepWithLines(
                step = FlowStep.PressKey(keyCode),
                lines = mergeLines(anchorLine)
            )
        }

    private fun parseSendBroadcast(
        anchorLine: TextLine,
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val attributeLines = readAnchorAttributesLines(allLines).bind()
            val attributes = parseAttributes(4, attributeLines).bind()

            val broadcast = if (attributes.hasName()) {
                attributes.name?.value?.getStringValue()?.bind()
            } else {
                anchor.value.getStringValue().bind()
            } ?: StringUtils.EMPTY

            if (broadcast.isBlank()) {
                raise(InvalidElementDataException(AnchorType.SEND_BROADCAST, anchorLine))
            }

            val packageName = broadcast.substringBefore("/")
            val action = broadcast.substringAfter("/")
            if (packageName.isBlank() || action.isBlank()) {
                raise(InvalidElementDataException(AnchorType.SEND_BROADCAST, anchorLine))
            }

            val keys = attributes.data?.keys
                ?.map { key -> key.value.getStringValue().bind() }
                ?: emptyList()

            val values = attributes.data?.values
                ?.map { value -> value.value.getStringValue().bind() }
                ?: emptyList()

            if (keys.size != values.size) {
                raise(InvalidElementDataException(AnchorType.SEND_BROADCAST, anchorLine))
            }

            val data = keys.zip(values).toMap()

            StepWithLines(
                step = FlowStep.SendBroadcast(
                    packageName = packageName,
                    action = action,
                    data = data,
                    condition = if (attributes.hasCondition()) {
                        attributes.getCondition().bind()
                    } else {
                        null
                    }
                ),
                lines = mergeLines(anchorLine, *attributeLines.toTypedArray())
            )
        }

    private fun parseAssert(
        anchorLine: TextLine,
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val attributeLines = readAnchorAttributesLines(allLines).bind()
            val attributes = parseAttributes(4, attributeLines).bind()

            val elements = when {
                !attributes.isEmpty() -> {
                    listOf(attributes.getUiSelector().bind())
                }

                anchor.value is AttributeValue.ListValue -> {
                    anchor.value.getValues().bind()
                        .map { value -> UiElementSelector.text(value) }
                }

                else -> {
                    listOf(UiElementSelector.text(anchor.value.getStringValue().bind()))
                }
            }

            if (elements.isEmpty()) {
                raise(InvalidLineFormatException(anchorLine))
            }

            val step = when (anchor.type) {
                AnchorType.ASSERT_VISIBLE -> FlowStep.AssertVisible(elements)
                AnchorType.ASSERT_NOT_VISIBLE -> FlowStep.AssertNotVisible(elements)
                else -> raise(InvalidLineFormatException(anchorLine))
            }

            StepWithLines(
                step = step,
                lines = mergeLines(anchorLine, *attributeLines.toTypedArray())
            )
        }

    private fun parseRunFlow(
        anchorLine: TextLine,
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val attributeLines = readAnchorAttributesLines(allLines).bind()
            val attributes = parseAttributes(4, attributeLines).bind()

            val path = anchor.value.getStringValue().bind()

            StepWithLines(
                step = FlowStep.RunFlow(
                    path = path,
                    condition = if (attributes.hasCondition()) {
                        attributes.getCondition().bind()
                    } else {
                        null
                    }
                ),
                lines = mergeLines(anchorLine)
            )
        }

    private fun parseWaitUntil(
        anchorLine: TextLine,
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val attributeLines = readAnchorAttributesLines(allLines).bind()
            val attributes = parseAttributes(4, attributeLines).bind()
            if (attributes.hasSelector()) {
                raise(InvalidElementDataException(AnchorType.WAIT_UNTIL, anchorLine))
            }

            val visible = attributes.visible
            val notVisible = attributes.notVisible

            val conditionType = when {
                visible != null -> ConditionType.VISIBLE
                notVisible != null -> ConditionType.NOT_VISIBLE
                else -> raise(InvalidElementDataException(AnchorType.WAIT_UNTIL, anchorLine))
            }

            val element = when {
                visible != null -> createUiElementSelector(
                    text = visible.text,
                    hasText = visible.hasText,
                    contentDescription = visible.contentDescription
                ).bind()

                notVisible != null -> createUiElementSelector(
                    text = notVisible.text,
                    hasText = notVisible.hasText,
                    contentDescription = notVisible.contentDescription
                ).bind()

                else -> raise(InvalidElementDataException(AnchorType.WAIT_UNTIL, anchorLine))
            }

            val step = attributes.step?.value?.getDurationValue()?.bind()
                ?: raise(InvalidElementDataException(AnchorType.WAIT_UNTIL, anchorLine))

            val timeout = attributes.timeout?.value?.getDurationValue()?.bind()
                ?: raise(InvalidElementDataException(AnchorType.WAIT_UNTIL, anchorLine))

            StepWithLines(
                step = FlowStep.WaitUntil(
                    conditionType = conditionType,
                    element = element,
                    step = step,
                    timeout = timeout
                ),
                lines = mergeLines(anchorLine, *attributeLines.toTypedArray())
            )
        }

    private fun parseInputText(
        anchorLine: TextLine,
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val attributeLines = readAnchorAttributesLines(allLines).bind()
            val attributes = parseAttributes(4, attributeLines).bind()

            val text = when {
                attributes.hasInput() -> attributes.input?.value?.getStringValue()?.bind()
                else -> anchor.value.getStringValue().bind()
            } ?: StringUtils.EMPTY

            if (text.isBlank()) {
                raise(InvalidElementDataException(AnchorType.INPUT_TEXT, anchorLine))
            }

            val element = if (attributes.hasSelector()) {
                attributes.getUiSelector().bind()
            } else {
                null
            }

            StepWithLines(
                step = FlowStep.InputText(
                    text = text,
                    element = element,
                    condition = if (attributes.hasCondition()) {
                        attributes.getCondition().bind()
                    } else {
                        null
                    }
                ),
                lines = mergeLines(anchorLine, *attributeLines.toTypedArray())
            )
        }

    private fun parseTap(
        anchorLine: TextLine,
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, StepWithLines> =
        either {
            val anchor = parseAnchorNode(anchorLine).bind()
            val attributeLines = readAnchorAttributesLines(allLines).bind()
            val attributes = parseAttributes(4, attributeLines).bind()

            val element = if (attributes.hasSelector()) {
                attributes.getUiSelector().bind()
            } else {
                UiElementSelector.text(anchor.value.getStringValue().bind())
            }

            val condition = if (attributes.hasCondition()) {
                attributes.getCondition().bind()
            } else {
                null
            }

            val isLong = (anchor.type == AnchorType.LONG_TAP_ON)

            val step = FlowStep.TapOn(
                element = element,
                isLong = isLong,
                condition = condition
            )

            StepWithLines(
                step = step,
                lines = mergeLines(anchorLine, *attributeLines.toTypedArray())
            )
        }

    private fun readAnchorAttributesLines(
        allLines: Deque<TextLine>
    ): Either<YamlParsingException, List<TextLine>> =
        either {
            val result = mutableListOf<TextLine>()

            while (allLines.isNotEmpty()) {
                val line = allLines.peekFirst()
                if (line.isBlank()) {
                    allLines.removeFirst()
                    continue
                }

                val key = line.keySubstring().bind()

                when {
                    !line.isAnchorLine() || key == AttributeType.KEY.key -> {
                        result.add(allLines.removeFirst())
                    }

                    else -> {
                        break
                    }
                }
            }

            result
        }

    private fun parseAttributes(
        startIndentLevel: Int,
        attributeLines: List<TextLine>
    ): Either<YamlParsingException, AttributeSet> =
        either {
            val lines = LinkedList(attributeLines)

            val attrs = AttributeSet()

            while (lines.isNotEmpty()) {
                val line = lines.removeFirst()

                val token = line.getAttributeToken().bind()
                if (token.indent < startIndentLevel) {
                    raise(InvalidIndentationException(line))
                }

                val attributeType = parseAttributeNode(line).bind().type

                when (attributeType) {
                    AttributeType.WHEN -> setWhenAttribute(attrs, line).bind()
                    AttributeType.VISIBLE -> setVisibleAttribute(attrs, line).bind()
                    AttributeType.NOT_VISIBLE -> setNotVisibleAttribute(attrs, line).bind()
                    AttributeType.TEXT -> setTextAttribute(attrs, line).bind()
                    AttributeType.HAS_TEXT -> setHasTextAttribute(attrs, line).bind()
                    AttributeType.DATA -> setDataAttribute(attrs, line).bind()
                    AttributeType.NAME -> setNameAttribute(attrs, line).bind()
                    AttributeType.KEY -> setDataKeyAttribute(attrs, line).bind()
                    AttributeType.VALUE -> setDataValueAttribute(attrs, line).bind()
                    AttributeType.CONTENT_DESCRIPTION -> setDescriptionAttribute(attrs, line).bind()
                    AttributeType.INPUT -> setInputAttribute(attrs, line)
                    AttributeType.STEP -> setStepAttribute(attrs, line).bind()
                    AttributeType.TIMEOUT -> setTimeoutAttribute(attrs, line).bind()
                }
            }

            attrs
        }

    private fun setWhenAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            if (result.whenAttribute != null) {
                raise(InvalidLineFormatException(line))
            }

            result.whenAttribute = WhenAttributes()
        }

    private fun setNotVisibleAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            if (result.whenAttribute?.notVisible != null ||
                result.whenAttribute?.visible != null ||
                result.visible != null ||
                result.notVisible != null
            ) {
                raise(InvalidLineFormatException(line))
            }

            val condition = ConditionAttributes()
            val attribute = parseAttributeNode(line).bind()
            val value = attribute.value.getStringValue().bind()
            if (value.isNotEmpty()) {
                condition.text = MutableAttributeNode(
                    type = AttributeType.TEXT,
                    value = AttributeValue.StringValue(value)
                )
            }

            if (result.whenAttribute != null) {
                result.whenAttribute?.notVisible = condition
            } else {
                result.notVisible = condition
            }
        }

    private fun setVisibleAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            if (result.whenAttribute?.notVisible != null ||
                result.whenAttribute?.visible != null ||
                result.visible != null ||
                result.notVisible != null
            ) {
                raise(InvalidLineFormatException(line))
            }

            val condition = ConditionAttributes()

            val node = parseAttributeNode(line).bind()
            val value = node.value.getStringValue().bind()
            if (value.isNotEmpty()) {
                condition.text = MutableAttributeNode(
                    type = AttributeType.TEXT,
                    value = AttributeValue.StringValue(value)
                )
            }

            if (result.whenAttribute != null) {
                result.whenAttribute?.visible = condition
            } else {
                result.visible = condition
            }
        }

    private fun setHasTextAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            val node = parseAttributeNode(line).bind()

            when {
                result.whenAttribute?.visible != null -> {
                    result.whenAttribute?.visible?.hasText = node
                }

                result.whenAttribute?.notVisible != null -> {
                    result.whenAttribute?.notVisible?.hasText = node
                }

                result.visible != null -> {
                    result.visible?.hasText = node
                }

                result.notVisible != null -> {
                    result.notVisible?.hasText = node
                }

                else -> {
                    result.hasText = node
                }
            }
        }

    private fun setTextAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            val node = parseAttributeNode(line).bind()

            when {
                result.whenAttribute?.visible != null -> {
                    result.whenAttribute?.visible?.text = node
                }

                result.whenAttribute?.notVisible != null -> {
                    result.whenAttribute?.notVisible?.text = node
                }

                result.visible != null -> {
                    result.visible?.text = node
                }

                result.notVisible != null -> {
                    result.notVisible?.text = node
                }

                else -> {
                    result.text = node
                }
            }
        }

    private fun setInputAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            val node = parseAttributeNode(line).bind()
            result.input = node
        }

    private fun setStepAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            result.step = parseAttributeNode(line).bind()
        }

    private fun setTimeoutAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            result.timeout = parseAttributeNode(line).bind()
        }

    private fun setDescriptionAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            val node = parseAttributeNode(line).bind()

            when {
                result.whenAttribute?.visible != null -> {
                    result.whenAttribute?.visible?.contentDescription = node
                }

                result.whenAttribute?.notVisible != null -> {
                    result.whenAttribute?.notVisible?.contentDescription = node
                }

                result.visible != null -> {
                    result.visible?.contentDescription = node
                }

                result.notVisible != null -> {
                    result.notVisible?.contentDescription = node
                }

                else -> {
                    result.contentDescription = node
                }
            }
        }

    private fun setNameAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            val node = parseAttributeNode(line).bind()
            result.name = node
        }

    private fun setDataAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            result.data = DataAttributes()
        }

    private fun setDataKeyAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            if (result.data == null) {
                raise(InvalidLineFormatException(line))
            }

            val node = parseAttributeNode(line).bind()
            result.data?.keys?.add(node)
        }

    private fun setDataValueAttribute(
        result: AttributeSet,
        line: TextLine
    ): Either<YamlParsingException, Unit> =
        either {
            if (result.data == null) {
                raise(InvalidLineFormatException(line))
            }

            val node = parseAttributeNode(line).bind()
            result.data?.values?.add(node)
        }

    private fun TextLine.isAnchorLine(): Boolean {
        return this.getAnchorToken() != null
    }

    private fun TextLine.getAttributeToken(): Either<YamlParsingException, AttributeToken> {
        val line = this

        return either {
            val startIndex = text.indexOfFirst { char -> !char.isWhitespace() }
            val colonIndex = text.indexOf(":")

            if (startIndex !in text.indices ||
                colonIndex !in text.indices ||
                colonIndex - startIndex <= 1
            ) {
                raise(InvalidLineFormatException(line))
            }

            if (text[startIndex] == '-') {
                if (text[startIndex + 1].isWhitespace() &&
                    startIndex + 2 < colonIndex &&
                    text.substring(startIndex + 2, colonIndex).isNotBlank()
                ) {
                    AttributeToken(
                        hasHyphen = true,
                        keyStartIndex = startIndex + 2,
                        colonIndex = colonIndex,
                        indent = text.getIndentCount()
                    )
                } else {
                    raise(InvalidLineFormatException(line))
                }
            } else {
                if (text.substring(startIndex, colonIndex).isNotBlank()) {
                    AttributeToken(
                        hasHyphen = false,
                        keyStartIndex = startIndex,
                        colonIndex = colonIndex,
                        indent = text.getIndentCount()
                    )
                } else {
                    raise(InvalidLineFormatException(line))
                }
            }
        }
    }

    private fun parseAttributeNode(line: TextLine): Either<YamlParsingException, AttributeNode> =
        either {
            val token = line.getAttributeToken().bind()

            val key = line.keySubstring().bind()
            val value = line.valueSubstring().bind()

            val type = getAttributeType(key)
                ?: raise(InvalidLineFormatException(line))

            val attributeValue = when {
                type == AttributeType.STEP -> parseDurationValue(line).bind()
                type == AttributeType.TIMEOUT -> parseDurationValue(line).bind()
                value.isNotBlank() -> AttributeValue.StringValue(value)
                else -> AttributeValue.Empty
            }

            MutableAttributeNode(
                type = type,
                value = attributeValue
            )
        }

    private fun TextLine.isAttributeLine(): Boolean {
        return this.getAttributeToken().getOrNull() != null
    }

    private fun String.splitByIndent(): Pair<String, String> {
        val indentIndex = this.getIndentCount()
        val indent = this.substring(0, indentIndex)
        return if (indentIndex < this.length) {
            indent to this.substring(indentIndex + 1)
        } else {
            indent to StringUtils.EMPTY
        }
    }

    private fun String.getIndentCount(): Int {
        var index = 0

        while (this[index].isWhitespace()) {
            index++
        }

        return index
    }

    private fun TextLine.getAnchorToken(): AnchorToken? {
        val hyphenIndex = text.indexOf("-")
        val colonIndex = text.indexOf(":")
        val spaceIndex = hyphenIndex + 1
        val indices = text.indices

        return if (hyphenIndex in indices &&
            colonIndex in indices &&
            spaceIndex in indices &&
            text[spaceIndex].isWhitespace() &&
            spaceIndex + 1 < colonIndex &&
            text.substring(spaceIndex + 1, colonIndex).isNotBlank()
        ) {
            AnchorToken(
                hyphenIndex = hyphenIndex,
                colonIndex = colonIndex
            )
        } else {
            null
        }
    }

    private fun parseAnchorNode(line: TextLine): Either<YamlParsingException, AnchorNode> =
        either {
            val token = line.getAnchorToken()
                ?: raise(InvalidLineFormatException(line))

            val key = line.keySubstring().bind()
            val type = getAnchorElementType(key)
                ?: raise(InvalidLineFormatException(line))

            val value = line.valueSubstring().bind()
            val attributeValue = when {
                value.isYamlList() -> parseListValue(line).bind()
                value.isNotBlank() -> AttributeValue.StringValue(value)
                else -> AttributeValue.Empty
            }

            AnchorNode(
                type = type,
                value = attributeValue
            )
        }

    private fun parseListValue(
        line: TextLine
    ): Either<YamlParsingException, AttributeValue.ListValue> =
        either {
            val value = line.valueSubstring().bind()

            if (!value.isYamlList()) {
                raise(InvalidLineFormatException(line))
            }

            AttributeValue.ListValue(
                values = value.substringBetween("[", "]")
                    .split(",")
                    .map { arrayValue -> arrayValue.trim() }
                    .filter { arrayValue -> arrayValue.isNotBlank() }
            )
        }

    private fun parseDurationValue(
        line: TextLine
    ): Either<YamlParsingException, AttributeValue.DurationValue> =
        either {
            val value = line.valueSubstring().bind()
                .replace(StringUtils.SPACE, StringUtils.EMPTY)
                .toIntSafely()
                ?: raise(InvalidLineFormatException(line))

            val duration = if (value >= 100) {
                Duration.millis(value)
            } else {
                Duration.seconds(value)
            }

            AttributeValue.DurationValue(duration)
        }

    private fun String.isYamlList(): Boolean {
        return startsWith("[") && endsWith("]")
    }

    private fun String.substringBetween(
        startDelimiter: String,
        endDelimiter: String
    ): String {
        val startIndex = indexOf(startDelimiter)
        val endIndex = indexOf(endDelimiter)

        return if (startIndex != -1 && startIndex < endIndex) {
            this.substring(startIndex + 1, endIndex)
        } else {
            StringUtils.EMPTY
        }
    }

    private fun getAnchorElementType(key: String): AnchorType? {
        return AnchorType.entries.firstOrNull { type -> type.key == key }
    }

    private fun getAttributeType(key: String): AttributeType? {
        return AttributeType.entries.firstOrNull { type -> type.key == key }
    }

    data class AnchorNode(
        val type: AnchorType,
        val value: AttributeValue
    )

    private fun AttributeSet.isEmpty(): Boolean {
        return data == null &&
            text == null &&
            contentDescription == null &&
            hasText == null &&
            input == null &&
            step == null &&
            timeout == null &&
            (whenAttribute == null || whenAttribute?.isEmpty() == true)
    }

    private fun AttributeSet.hasInput(): Boolean = input != null

    private fun AttributeSet.hasSelector(): Boolean =
        text != null || contentDescription != null || hasText != null

    private fun AttributeSet.hasName(): Boolean = name != null

    private fun AttributeSet.hasCondition(): Boolean {
        return whenAttribute != null && whenAttribute?.isEmpty() == false
    }

    private fun WhenAttributes.isEmpty(): Boolean {
        return visible == null && notVisible == null
    }

    private fun AttributeSet.getCondition(): Either<YamlParsingException, FlowStepPrecondition> =
        either {
            val visible = whenAttribute?.visible
            val notVisible = whenAttribute?.notVisible

            val type = when {
                visible != null -> ConditionType.VISIBLE
                notVisible != null -> ConditionType.NOT_VISIBLE
                else -> raise(YamlParsingException("Invalid condition"))
            }

            val selector = when {
                visible != null -> createUiElementSelector(
                    text = visible.text,
                    hasText = visible.hasText,
                    contentDescription = visible.contentDescription
                ).bind()

                notVisible != null -> createUiElementSelector(
                    text = notVisible.text,
                    hasText = notVisible.hasText,
                    contentDescription = notVisible.contentDescription
                ).bind()

                else -> raise(YamlParsingException("Invalid condition"))
            }

            FlowStepPrecondition(
                type = type,
                element = selector
            )
        }

    private fun createUiElementSelector(
        text: AttributeNode?,
        contentDescription: AttributeNode?,
        hasText: AttributeNode?
    ): Either<YamlParsingException, UiElementSelector> =
        either {
            when {
                text != null -> UiElementSelector.text(
                    text = text.value.getStringValue().bind()
                )

                contentDescription != null -> UiElementSelector.contentDescription(
                    contentDescription = contentDescription.value.getStringValue().bind()
                )

                hasText != null -> UiElementSelector.containsText(
                    text = hasText.value.getStringValue().bind()
                )

                else -> raise(YamlParsingException(message = "Invalid selector"))
            }
        }

    private fun AttributeSet.getUiSelector(): Either<YamlParsingException, UiElementSelector> =
        createUiElementSelector(
            text = text,
            contentDescription = contentDescription,
            hasText = hasText
        )

    private fun mergeLines(vararg lines: TextLine): TextLineRange {
        return TextLineRange(
            start = lines.minOf { line -> line.number },
            end = lines.maxOf { line -> line.number },
            lines = lines.map { line -> line }
        )
    }

    private fun AttributeValue.getDurationValue(): Either<YamlParsingException, Duration> {
        val value = this

        return either {
            when (value) {
                is AttributeValue.DurationValue -> value.duration
                else -> raise(YamlParsingException("Invalid attribute value: $value"))
            }
        }
    }

    private fun AttributeValue.getStringValue(): Either<YamlParsingException, String> {
        val value = this

        return either {
            when (value) {
                AttributeValue.Empty -> StringUtils.EMPTY
                is AttributeValue.StringValue -> value.value
                else -> raise(YamlParsingException("Invalid attribute value: $value"))
            }
        }
    }

    private fun AttributeValue.getValues(): Either<YamlParsingException, List<String>> {
        val value = this

        return either {
            when (value) {
                AttributeValue.Empty -> emptyList()
                is AttributeValue.ListValue -> value.values
                else -> raise(YamlParsingException("Invalid attribute value: $value"))
            }
        }
    }

    sealed interface AttributeValue {

        data object Empty : AttributeValue

        data class StringValue(
            val value: String
        ) : AttributeValue

        data class ListValue(
            val values: List<String>
        ) : AttributeValue

        data class DurationValue(
            val duration: Duration
        ) : AttributeValue
    }

    enum class AnchorType(val key: String) {
        NAME("name"),
        PROJECT("project"),
        GROUP("group"),
        LAUNCH("launch"),
        SEND_BROADCAST("sendBroadcast"),
        TAP_ON("tapOn"),
        LONG_TAP_ON("longTapOn"),
        INPUT_TEXT("inputText"),
        RUN_FLOW("runFlow"),
        ASSERT_VISIBLE("assertVisible"),
        ASSERT_NOT_VISIBLE("assertNotVisible"),
        WAIT_UNTIL("waitUntil"),
        PRESS_KEY("pressKey")
    }

    enum class AttributeType(val key: String) {
        TEXT("text"),
        CONTENT_DESCRIPTION("contentDescription"),
        HAS_TEXT("hasText"),
        NAME("name"),
        DATA("data"),
        KEY("key"),
        VALUE("value"),
        WHEN("when"),
        VISIBLE("visible"),
        NOT_VISIBLE("notVisible"),
        INPUT("input"),
        STEP("step"),
        TIMEOUT("timeout")
    }

    data class AnchorToken(
        val hyphenIndex: Int,
        val colonIndex: Int
    )

    data class AttributeToken(
        val hasHyphen: Boolean,
        val keyStartIndex: Int,
        val colonIndex: Int,
        val indent: Int
    )

    data class AttributeSet(
        var data: DataAttributes? = null,
        var name: AttributeNode? = null,
        var text: AttributeNode? = null,
        var contentDescription: AttributeNode? = null,
        var hasText: AttributeNode? = null,
        var input: AttributeNode? = null,
        var step: AttributeNode? = null,
        var timeout: AttributeNode? = null,
        var whenAttribute: WhenAttributes? = null,
        var visible: ConditionAttributes? = null,
        var notVisible: ConditionAttributes? = null
    )

    data class DataAttributes(
        var keys: MutableList<AttributeNode> = mutableListOf(),
        var values: MutableList<AttributeNode> = mutableListOf()
    )

    data class WhenAttributes(
        var visible: ConditionAttributes? = null,
        var notVisible: ConditionAttributes? = null
    )

    data class ConditionAttributes(
        var text: AttributeNode? = null,
        var contentDescription: AttributeNode? = null,
        var hasText: AttributeNode? = null
    )

    interface AttributeNode {
        val type: AttributeType
        val value: AttributeValue
    }

    data class MutableAttributeNode(
        override val type: AttributeType,
        override var value: AttributeValue
    ) : AttributeNode

    data class StepWithLines(
        val step: FlowStep,
        val lines: TextLineRange
    )

    companion object {
        const val KEY_BACK = "back"
        const val KEY_HOME = "home"

        private val KEY_CODES = mapOf(
            KEY_BACK to KeyCode.Back,
            KEY_HOME to KeyCode.Home
        )
    }
}