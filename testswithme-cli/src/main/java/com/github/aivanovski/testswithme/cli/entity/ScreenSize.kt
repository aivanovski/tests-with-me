package com.github.aivanovski.testswithme.cli.entity

data class ScreenSize(
    val width: Int,
    val height: Int
) {

    companion object {
        val DEFAULT = ScreenSize(
            width = 56,
            height = 32
        )
    }
}