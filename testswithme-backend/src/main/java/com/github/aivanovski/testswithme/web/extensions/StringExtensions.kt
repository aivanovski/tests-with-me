package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.web.entity.TextChunk
import com.github.aivanovski.testswithme.web.entity.Uid
import kotlin.math.min

const val CHUNK_SIZE_IN_BYTES = 1024

fun String.splitIntoParts(
    partLength: Int
): List<String> {
    return if (this.length > partLength) {
        val parts = mutableListOf<String>()

        var index = 0
        while (index < this.length) {
            val end = min(this.length, index + partLength)
            parts.add(this.substring(index, end))
            index = end
        }

        parts
    } else {
        listOf(this)
    }
}

fun String.splitIntoChunks(
    entityUid: Uid,
    chunkSize: Int = CHUNK_SIZE_IN_BYTES
): List<TextChunk> {
    return this.splitIntoParts(chunkSize)
        .mapIndexed { index, chunk ->
            TextChunk(
                entityUid = entityUid,
                chunkIndex = index,
                content = chunk
            )
        }
}