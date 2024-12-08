package com.github.aivanovski.testswithme.extensions

import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.KeyCode
import java.lang.StringBuilder

// TODO: Remove describe method from Commands (see Assert, Broadcast, etc)

fun FlowStep.isStepFlaky(): Boolean {
    return this is FlowStep.AssertVisible ||
        this is FlowStep.AssertNotVisible ||
        this is FlowStep.TapOn
}

fun FlowStep.describe(): String {
    return when (this) {
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
}

private fun FlowStep.Launch.describe(): String {
    return "Launch app: package name = %s".format(packageName)
}

private fun FlowStep.SendBroadcast.describe(): String {
    return StringBuilder()
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
        }
        .toString()
}

private fun FlowStep.TapOn.describe(): String {
    return "%s on element: %s".format(
        if (isLong) "Long Tap" else "Tap",
        element.toReadableFormat()
    )
}

private fun FlowStep.AssertVisible.describe(): String {
    val assertion = "is visible"
    return when {
        else -> "Assert %s: %s".format(assertion, elements.toReadableFormat())
    }
}

private fun FlowStep.AssertNotVisible.describe(): String {
    val assertion = "is not visible"
    return when {
        else -> "Assert %s: %s".format(assertion, elements.toReadableFormat())
    }
}

private fun FlowStep.InputText.describe(): String {
    return if (element != null) {
        "Input text: [%s] into %s".format(text, element.toReadableFormat())
    } else {
        "Input text: [%s]".format(text)
    }
}

private fun FlowStep.PressKey.describe(): String {
    val name = when (key) {
        KeyCode.Back -> "Back"
        KeyCode.Home -> "Home"
    }

    return "Press key: %s".format(name)
}

private fun FlowStep.WaitUntil.describe(): String {
    return String.format(
        "Wait for element: %s, timeout = %s, step = %s",
        element.toReadableFormat(),
        timeout.toReadableFormat(),
        step.toReadableFormat()
    )
}

private fun FlowStep.RunFlow.describe(): String {
    return "Run flow '%s'".format(path)
}