package com.github.aivanovski.testswithme.web.entity

data class Response<T>(
    val response: T,
    val headers: List<Pair<String, String>> = emptyList()
)