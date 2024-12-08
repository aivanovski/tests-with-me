package com.github.aivanovski.testswithme.entity

import com.github.aivanovski.testswithme.extensions.splitToPair

data class Hash(
    val type: HashType,
    val value: String
) {

    companion object {

        fun formatToString(hash: Hash): String = "${hash.type.name}:${hash.value}"

        fun fromString(str: String): Hash? {
            val (typeStr, value) = str.splitToPair(separator = ":") ?: return null
            val type = HashType.findByName(typeStr) ?: return null
            return Hash(type, value)
        }
    }
}