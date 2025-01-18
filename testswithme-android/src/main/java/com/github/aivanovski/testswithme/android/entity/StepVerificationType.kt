package com.github.aivanovski.testswithme.android.entity

enum class StepVerificationType {
    LOCAL,
    REMOTE;

    companion object {

        fun fromName(value: String): StepVerificationType? {
            return StepVerificationType.values()
                .firstOrNull { type -> type.name == value }
        }
    }
}