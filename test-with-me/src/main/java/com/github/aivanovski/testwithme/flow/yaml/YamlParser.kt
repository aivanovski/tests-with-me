package com.github.aivanovski.testwithme.flow.yaml

import arrow.core.Either
import arrow.core.raise.either
import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.aivanovski.testwithme.entity.Duration
import com.github.aivanovski.testwithme.entity.KeyCode
import com.github.aivanovski.testwithme.entity.Flow
import com.github.aivanovski.testwithme.entity.FlowStep
import com.github.aivanovski.testwithme.entity.UiElementSelector
import com.github.aivanovski.testwithme.entity.exception.ParsingException
import com.github.aivanovski.testwithme.extensions.toLongSafely
import com.github.aivanovski.testwithme.utils.StringUtils.EMPTY
import com.github.aivanovski.testwithme.utils.StringUtils.SPACE

class YamlParser {

    fun parse(
        data: String
    ): Either<ParsingException, Flow> = either {
        val mapper = ObjectMapper(YAMLFactory())
            .registerModule(KotlinModule.Builder().build())

        val items = try {
            mapper.readValue<List<Item>>(data)
        } catch (exception: JacksonException) {
            raise(ParsingException(cause = exception))
        }

        val name = findNameItem(items)
        val filteredItems = items.filter { item -> item != name }

        val steps = convertItems(filteredItems).bind()

        Flow(
            name = name?.name ?: EMPTY,
            steps = steps
        )
    }

    private fun findNameItem(items: List<Item>): Item? {
        return items.firstOrNull { item -> !item.name.isNullOrEmpty() }
    }

    private fun convertItems(
        items: List<Item>
    ): Either<ParsingException, List<FlowStep>> = either {
        val result = mutableListOf<FlowStep>()

        for (item in items) {
            val parsedItem = parseItem(item).bind()
            result.add(parsedItem)
        }

        result
    }

    private fun parseItem(item: Item): Either<ParsingException, FlowStep> {
        return when {
            item.isSendBroadcast() -> parseSendBroadcast(item)
            item.isLaunch() -> parseLaunch(item)
            item.isAssertVisible() -> parseAssertVisible(item)
            item.isAssertNotVisible() -> parseAssertNotVisible(item)
            item.isTapOn() -> parseTapOn(item)
            item.isLongTapOn() -> parseLongTapOn(item)
            item.isInputText() -> parseInputText(item)
            item.isPressKey() -> parsePressKey(item)
            item.isWaitUntil() -> parseWaitUntil(item)
            item.isRunFlow() -> parseRunFlow(item)
            else -> Either.Left(ParsingException("Unable to parse item: $item"))
        }
    }

    private fun Item.isSendBroadcast(): Boolean {
        return isStringField(sendBroadcast)
    }

    private fun Item.isLaunch(): Boolean {
        return isStringField(launch)
    }

    private fun Item.isAssertVisible(): Boolean {
        return isStringField(assertVisible) || isMapField(assertVisible)
    }

    private fun Item.isAssertNotVisible(): Boolean {
        return isStringField(assertNotVisible) || isMapField(assertNotVisible)
    }

    private fun Item.isTapOn(): Boolean {
        return isStringField(tapOn) || isMapField(tapOn)
    }

    private fun Item.isLongTapOn(): Boolean {
        return isStringField(longTapOn) || isMapField(longTapOn)
    }

    private fun Item.isInputText(): Boolean {
        return isStringField(inputText) || isMapField(inputText)
    }

    private fun Item.isPressKey(): Boolean {
        return isStringField(pressKey)
    }

    private fun Item.isWaitUntil(): Boolean {
        return isMapField(waitUntil)
    }

    private fun Item.isRunFlow(): Boolean {
        return isStringField(runFlow)
    }

    private fun parseSendBroadcast(item: Item): Either<ParsingException, FlowStep> {
        val values = item.sendBroadcast
            ?.split("/")
            ?.filter { text -> text.isNotEmpty() }
            ?: emptyList()
        if (values.size != 2) {
            return Either.Left(ParsingException("Unable to parse item: $item"))
        }

        val data = item.data
            ?.mapNotNull { dataItem ->
                val key = dataItem.key
                val value = dataItem.value ?: EMPTY
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    key to value
                } else {
                    null
                }
            }
            ?.toMap()
            ?: emptyMap()

        return Either.Right(
            FlowStep.SendBroadcast(
                packageName = values[0],
                action = values[1],
                data = data
            )
        )
    }

    private fun parseLaunch(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        FlowStep.Launch(
            packageName = item.launch ?: EMPTY
        )
    }

    private fun parseAssertVisible(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        val element = parseUiElement(item.assertVisible).bind()

        FlowStep.AssertVisible(
            elements = listOf(element)
        )
    }

    private fun parseAssertNotVisible(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        val element = parseUiElement(item.assertNotVisible).bind()

        FlowStep.AssertNotVisible(
            elements = listOf(element)
        )
    }

    private fun parseTapOn(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        FlowStep.TapOn(
            element = parseUiElement(item.tapOn).bind()
        )
    }

    private fun parseLongTapOn(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        FlowStep.TapOn(
            element = parseUiElement(item.longTapOn).bind(),
            isLong = true
        )
    }

    private fun parsePressKey(item: Item): Either<ParsingException, FlowStep> {
        val name = item.pressKey
            ?: return Either.Left(ParsingException("Button name is not specified"))

        val keyCode = KEY_CODES[name.lowercase()]
            ?: return Either.Left(ParsingException("Invalid button key specified: $name"))

        return Either.Right(
            FlowStep.PressKey(
                key = keyCode
            )
        )
    }

    private fun parseInputText(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        when (val inputText = item.inputText) {
            is String -> {
                FlowStep.InputText(
                    text = inputText,
                    element = null
                )
            }

            is Map<*, *> -> {
                val element = parseUiElement(inputText).bind()

                FlowStep.InputText(
                    text = (inputText[INPUT] as? String) ?: EMPTY,
                    element = element
                )
            }

            else -> raise(ParsingException("Unable to parse item: $item"))
        }
    }

    private fun parseRunFlow(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        FlowStep.RunFlow(
            flowUid = item.runFlow ?: EMPTY
        )
    }

    private fun parseWaitUntil(
        item: Item
    ): Either<ParsingException, FlowStep> = either {
        val values = item.waitUntil as? Map<*, *>
            ?: raise(ParsingException("Unable to parse item: $item"))

        val element = parseUiElement(values).bind()

        val stepStr = values[STEP]
        val timeoutStr = values[TIMEOUT]
            ?: raise(ParsingException("Parameter '$TIMEOUT' should be specified"))

        val step = stepStr?.let { parseDuration(stepStr) }
            ?: Duration.seconds(1)

        val timeout = parseDuration(timeoutStr)
            ?: raise(ParsingException("Unable to parse duration: $timeoutStr"))

        FlowStep.WaitUntil(
            element = element,
            step = step,
            timeout = timeout
        )
    }

    private fun parseUiElement(
        element: Any?
    ): Either<ParsingException, UiElementSelector> {
        if (element !is String && element !is Map<*, *>) {
            return Either.Left(ParsingException("Unable to parse UiElement: $element"))
        }

        val result = when (element) {
            is String -> {
                UiElementSelector.text(element)
            }

            is Map<*, *> -> {
                val id = element[ID] as? String
                val text = element[TEXT] as? String
                val cd = element[CONTENT_DESCRIPTION] as? String
                val hasText = element[HAS_TEXT] as? String

                when {
                    !id.isNullOrEmpty() -> UiElementSelector.id(id)
                    !text.isNullOrEmpty() -> UiElementSelector.text(text)
                    !cd.isNullOrEmpty() -> UiElementSelector.contentDescription(cd)
                    !hasText.isNullOrEmpty() -> UiElementSelector.containsText(hasText)
                    else -> {
                        return Either.Left(ParsingException("Unable to parse UiElement: $element"))
                    }
                }
            }

            else -> throw IllegalStateException()
        }

        return Either.Right(result)
    }

    private fun parseDuration(value: Any): Duration? {
        val longValue = when (value) {
            is Int -> value.toLong()
            is String -> value.replace(SPACE, EMPTY).toLongSafely()
            else -> return null
        } ?: return null

        return if (longValue >= 100) {
            Duration.millis(longValue)
        } else {
            Duration.seconds(longValue.toInt())
        }
    }

    private fun isStringField(value: Any?): Boolean {
        return value is String && value.isNotEmpty()
    }

    private fun isMapField(value: Any?): Boolean {
        return value is Map<*, *>
    }

    internal data class Item(
        var name: String? = null,
        var sendBroadcast: String? = null,
        var data: List<Data>? = null,
        var launch: String? = null,
        var assertVisible: Any? = null,
        var assertNotVisible: Any? = null,
        var tapOn: Any? = null,
        var longTapOn: Any? = null,
        var inputText: Any? = null,
        var pressKey: String? = null,
        var waitUntil: Any? = null,
        var runFlow: String? = null
    )

    internal data class Data(
        var key: String,
        var value: String?
    )

    companion object {
        private const val ID = "id"
        private const val TEXT = "text"
        private const val CONTENT_DESCRIPTION = "contentDescription"
        private const val HAS_TEXT = "hasText"
        private const val INPUT = "input"
        private const val STEP = "step"
        private const val TIMEOUT = "timeout"

        private val KEY_CODES = mapOf(
            "back" to KeyCode.Back,
            "home" to KeyCode.Home
        )
    }
}