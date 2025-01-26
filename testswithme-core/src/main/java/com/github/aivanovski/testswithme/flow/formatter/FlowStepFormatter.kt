package com.github.aivanovski.testswithme.flow.formatter

import com.github.aivanovski.testswithme.entity.ConditionType
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.FlowStepPrecondition
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.extensions.toReadableFormat
import com.github.aivanovski.testswithme.utils.StringUtils.NEW_LINE

class FlowStepFormatter {

    fun format(
        step: FlowStep,
        format: Format
    ): String {
        val title = step.formatTitle()
        val description = step.formatDescription(format)

        return buildString {
            append(title)

            if (description.isNotEmpty()) {
                append(NEW_LINE)
                append(description)
            }
        }
    }

    private fun FlowStep.formatTitle(): String =
        when (this) {
            is FlowStep.Launch -> "- launch:"
            is FlowStep.SendBroadcast -> "- sendBroadcast:"
            is FlowStep.TapOn -> if (isLong) "- longTapOn:" else "- tapOn:"
            is FlowStep.AssertVisible -> "- assertVisible:"
            is FlowStep.AssertNotVisible -> "- assertNotVisible:"
            is FlowStep.InputText -> "- inputText:"
            is FlowStep.PressKey -> "- pressKey:"
            is FlowStep.WaitUntil -> "- waitUntil:"
            is FlowStep.RunFlow -> "- runFlow:"
        }

    private fun FlowStep.formatDescription(format: Format): String =
        when (this) {
            is FlowStep.Launch -> this.formatLaunch()
            is FlowStep.SendBroadcast -> this.formatSendBroadcast(format)
            is FlowStep.TapOn -> this.formatTapOn()
            is FlowStep.AssertVisible -> this.formatAssertVisible()
            is FlowStep.AssertNotVisible -> this.toString()
            is FlowStep.InputText -> this.formatInputText(format)
            is FlowStep.PressKey -> this.toString()
            is FlowStep.WaitUntil -> this.formatWaitUntil(format)
            is FlowStep.RunFlow -> this.formatDescription(format)
        }

    private fun FlowStep.WaitUntil.formatWaitUntil(format: Format): String =
        buildString {
            append(INDENT)

            when (conditionType) {
                ConditionType.VISIBLE -> append("- visible:")
                ConditionType.NOT_VISIBLE -> append("- notVisible:")
            }

            append(NEW_LINE)
            append(INDENT.repeat(2))
            append(element.format())

            append(NEW_LINE)
            append(INDENT)
            append("- step: ${step.toReadableFormat()}")

            append(NEW_LINE)
            append(INDENT)
            append("- timeout: ${timeout.toReadableFormat()}")
        }

    private fun FlowStep.InputText.formatInputText(format: Format): String =
        buildString {
            append(INDENT)
            append("- text: $text")

            if (element != null) {
                append(NEW_LINE)
                append(INDENT)
                append(element.format())
            }

            if (condition != null) {
                append(NEW_LINE)
                append(INDENT)
                append(condition.format())
            }
        }

    private fun FlowStep.AssertVisible.formatAssertVisible(): String =
        buildString {
            append(INDENT)
            // TODO: implement other elements
            append(elements.first().format())
        }

    private fun FlowStep.TapOn.formatTapOn(): String =
        buildString {
            append(INDENT)
            append(element.format())
        }

    private fun FlowStep.Launch.formatLaunch(): String = packageName

    private fun FlowStep.RunFlow.formatDescription(format: Format): String =
        buildString {
            append(INDENT)
            append(path)

            if (condition != null) {
                append(NEW_LINE)
                append(INDENT)
                append(condition.format())
            }
        }

    private fun FlowStep.SendBroadcast.formatSendBroadcast(format: Format): String =
        buildString {
            append(INDENT)
            append(packageName)
            append("/")
            append(action)

            when (format) {
                Format.SHORT -> {
                    if (data.isNotEmpty()) {
                        append(NEW_LINE)
                        append(INDENT)
                        append("- data: [...]")
                    }
                }

                Format.FULL -> {
                    if (data.isNotEmpty()) {
                        append(NEW_LINE)
                        append(INDENT)
                        append("- data:")

                        for ((key, value) in data) {
                            append(NEW_LINE)
                            append(INDENT.repeat(2))
                            append("- key: $key")

                            append(NEW_LINE)
                            append(INDENT.repeat(2))
                            append("- value: $value")
                        }
                    }
                }
            }

            if (condition != null) {
                append(NEW_LINE)
                append(INDENT)
                append(condition.format())
            }
        }

    private fun FlowStepPrecondition.format(): String = this.toString() // TODO: implement

    private fun UiElementSelector.format(): String =
        buildString {
            if (text != null) {
                append("- text: $text")
            }

            if (containsText != null) {
                append("- hasText: $containsText")
            }

            if (contentDescription != null) {
                append("- contentDescription: $contentDescription")
            }
        }

    enum class Format {
        SHORT,
        FULL
    }

    companion object {
        private val INDENT = ""
    }
}