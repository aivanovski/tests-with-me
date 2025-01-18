package com.github.aivanovski.testswithme.web.entity

enum class NetworkProtocolType {
    HTTP,
    HTTPS;

    companion object {

        fun default() = HTTPS

        fun getByName(name: String): NetworkProtocolType? =
            entries.firstOrNull { type -> name.contentEquals(type.name, ignoreCase = true) }
    }
}