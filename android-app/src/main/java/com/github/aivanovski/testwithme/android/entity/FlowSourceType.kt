package com.github.aivanovski.testwithme.android.entity

enum class FlowSourceType {
    LOCAL,
    REMOTE;

    companion object {
        fun fromName(name: String): FlowSourceType? {
            return values().firstOrNull { type -> type.name == name }
        }
    }
}