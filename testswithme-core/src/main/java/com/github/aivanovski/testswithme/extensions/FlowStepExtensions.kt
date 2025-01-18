package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.entity.ConditionType
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.FlowStepPrecondition
import com.github.aivanovski.testswithme.entity.KeyCode
import java.lang.StringBuilder

// TODO: Remove describe method from Commands (see Assert, Broadcast, etc)

fun FlowStep.isStepFlaky(): Boolean {
    return this is FlowStep.AssertVisible ||
        this is FlowStep.AssertNotVisible ||
        this is FlowStep.TapOn
}

fun FlowStep.describe(): String =
    when (this) {
        is FlowStep.Launch -> describe()
        is FlowStep.SendBroadcast -> describe()
        is FlowStep.TapOn -> describe()
        is FlowStep.AssertVisible -> describe()
        is FlowStep.AssertNotVisible -> describe()
        is FlowStep.InputText -> describe()
        is FlowStep.PressKey -> describe()
        is FlowStep.WaitUntil -> describe()
        is FlowStep.RunFlow -> describe()
    }

private fun FlowStepPrecondition.describe(): String =
    StringBuilder()
        .apply {
            val condition = when (type) {
                ConditionType.VISIBLE -> "is visible"
                ConditionType.NOT_VISIBLE -> "is not visible"
            }

            append("WHEN %s %s".format(element.toReadableFormat(), condition))
        }
        .toString()

private fun FlowStep.Launch.describe(): String = "Launch app: package name = %s".format(packageName)

private fun FlowStep.SendBroadcast.describe(): String =
    StringBuilder()
        .apply {
            append("Broadcast: $packageName/$action")
            if (data.isNotEmpty()) {
                append(" [")

                for ((key, value) in data.entries) {
                    if (!endsWith("[")) {
                        append(", ")
                    }
                    append("$key=$value")
                }

                append("]")
            }

            if (condition != null) {
                append(" %s".format(condition.describe()))
            }
        }
        .toString()

private fun FlowStep.TapOn.describe(): String =
    StringBuilder()
        .apply {
            val tapName = if (isLong) "Long Tap" else "Tap"

            append("%s on element: %s".format(tapName, element.toReadableFormat()))

            if (condition != null) {
                append(" %s".format(condition.describe()))
            }
        }
        .toString()

private fun FlowStep.AssertVisible.describe(): String =
    "Assert %s: %s".format("is visible", elements.toReadableFormat())

private fun FlowStep.AssertNotVisible.describe(): String =
    "Assert %s: %s".format("is not visible", elements.toReadableFormat())

private fun FlowStep.InputText.describe(): String =
    StringBuilder()
        .apply {
            if (element != null) {
                append("Input text: [%s] into %s".format(text, element.toReadableFormat()))
            } else {
                append("Input text: [%s]".format(text))
            }

            if (condition != null) {
                append(" %s".format(condition.describe()))
            }
        }
        .toString()

private fun FlowStep.PressKey.describe(): String =
    StringBuilder()
        .apply {
            val keyName = when (key) {
                KeyCode.Back -> "Back"
                KeyCode.Home -> "Home"
            }

            append("Press key: %s".format(keyName))

            if (condition != null) {
                append(" %s".format(condition.describe()))
            }
        }
        .toString()

private fun FlowStep.WaitUntil.describe(): String =
    String.format(
        "Wait for element: %s, timeout = %s, step = %s",
        element.toReadableFormat(),
        timeout.toReadableFormat(),
        step.toReadableFormat()
    )

private fun FlowStep.RunFlow.describe(): String =
    StringBuilder()
        .apply {
            append("Run flow '%s'".format(path))

            if (condition != null) {
                append(" %s".format(condition.describe()))
            }
        }
        .toString()