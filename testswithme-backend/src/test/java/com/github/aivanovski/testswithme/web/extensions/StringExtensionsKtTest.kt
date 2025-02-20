package com.github.aivanovski.testswithme.web.extensions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class StringExtensionsKtTest {

    @Test
    fun splitIntoParts() {
        "A".repeat(3)
            .splitIntoParts(partLength = 1) shouldBe listOf("A", "A", "A")

        "ABCD".repeat(3)
            .splitIntoParts(partLength = 4) shouldBe listOf("ABCD", "ABCD", "ABCD")

        ("ABCD".repeat(3) + "ABC")
            .splitIntoParts(partLength = 4) shouldBe listOf("ABCD", "ABCD", "ABCD", "ABC")
    }
}