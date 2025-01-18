package com.github.aivanovski.testswithme.entity

enum class HashType {
    SHA_256;

    companion object {

        fun findByName(name: String): HashType? =
            HashType.values().firstOrNull { type -> type.name == name }
    }
}