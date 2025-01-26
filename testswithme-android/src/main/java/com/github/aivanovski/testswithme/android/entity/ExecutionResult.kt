package com.github.aivanovski.testswithme.android.entity

enum class ExecutionResult {
    NONE,
    SUCCESS,
    FAILED;

    companion object {

        fun fromName(name: String): ExecutionResult? =
            entries.firstOrNull { status -> status.name == name }

        fun fromBoolean(isPassed: Boolean?): ExecutionResult =
            when (isPassed) {
                true -> SUCCESS
                false -> FAILED
                else -> NONE
            }
    }
}