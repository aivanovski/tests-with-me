package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.entity.Bounds
import kotlin.math.roundToInt

class AsciScreen(
    val pixelWidth: Int,
    val pixelHeight: Int,
    val width: Int,
    val height: Int
) {

    val widthRange = 0 until width
    val heightRange = 0 until height

    private val buffer = createBuffer()

    fun convertBounds(pixelBounds: Bounds): Bounds? {
        val widthScale = width / pixelWidth.toFloat()
        val heightScale = height / pixelHeight.toFloat()

        val left = (pixelBounds.left * widthScale).roundToInt()
        val right = (pixelBounds.right * widthScale).roundToInt()
        var top = (pixelBounds.top * heightScale).roundToInt()
        var bottom = (pixelBounds.bottom * heightScale).roundToInt()

        val lines = (bottom - top)

        if (top == bottom && top == height - 1) {
            return null
        }

        when {
            lines == 0 -> {
                top = heightRange.limit(top - 1)
                bottom = heightRange.limit(bottom + 1)
            }

            lines == 1 -> {
                top = heightRange.limit(top - 1)
            }
        }

        return Bounds(left, top, right, bottom)
    }

    fun clear() {
        for (y in heightRange) {
            for (x in widthRange) {
                buffer[y][x] = SPACE
            }
        }
    }

    fun render(nodes: List<Node>) {
        drawFrame()
        drawHorizontalLines(nodes)
        drawVerticalLines(nodes)
        drawText(nodes)
    }

    private fun drawFrame() {
        for (x in widthRange) {
            buffer[0][x] = '-'
            buffer[height - 1][x] = '-'
        }

        for (y in heightRange) {
            buffer[y][0] = '|'
            buffer[y][width - 1] = '|'
        }
    }

    private fun drawHorizontalLines(nodes: List<Node>) {
        for (node in nodes) {
            val left = widthRange.limit(node.bounds.left)
            val right = widthRange.limit(node.bounds.right)

            val top = heightRange.limit(node.bounds.top)
            val bottom = heightRange.limit(node.bounds.bottom)

            // Draw top and bottom lines
            for (i in left..right) {
                if (buffer[top][i] == ' ') {
                    buffer[top][i] = '-'
                }

                if (buffer[bottom][i] == ' ') {
                    buffer[bottom][i] = '-'
                }
            }
        }
    }

    private fun drawVerticalLines(nodes: List<Node>) {
        for (node in nodes) {
            val left = widthRange.limit(node.bounds.left)
            val right = widthRange.limit(node.bounds.right)

            val top = heightRange.limit(node.bounds.top)
            val bottom = heightRange.limit(node.bounds.bottom)

            // Draw top and bottom lines
            for (i in top..bottom) {
                val ch = when {
                    i == top -> '+'
                    i == bottom -> '+'
                    else -> '|'
                }

                buffer[i][left] = ch
                buffer[i][right] = ch
            }
        }
    }

    private fun drawText(nodes: List<Node>) {
        for (node in nodes) {
            val left = widthRange.limit(node.bounds.left)
            val right = widthRange.limit(node.bounds.right)

            val top = heightRange.limit(node.bounds.top)
            val bottom = heightRange.limit(node.bounds.bottom)
            val lines = bottom - top
            val middle = if (lines > 1) {
                top + lines / 2
            } else {
                top + lines
            }

            val textLeft = left + 1
            val textRight = right - 1
            for (i in textLeft..textRight) {
                val ch = node.text.getOrNull(i - textLeft) ?: continue

                buffer[middle][i] = ch
            }
        }
    }

    private fun IntRange.limit(value: Int): Int {
        return if (value < start) {
            start
        } else if (value > last) {
            last
        } else {
            value
        }
    }

    fun getContent(): String {
        val lines = mutableListOf<String>()
        for (y in 0 until height) {
            lines.add(String(buffer[y]))
        }

        return lines.joinToString(separator = "\n")
    }

    private fun createBuffer(): Array<CharArray> {
        return Array(
            height,
            init = { CharArray(width, init = { SPACE }) }
        )
    }

    data class Node(
        val text: String,
        val bounds: Bounds
    )

    companion object {
        private const val SPACE = ' '
    }
}