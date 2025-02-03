package com.github.aivanovski.testswithme.domain.fomatters

import com.github.aivanovski.testswithme.domain.fomatters.TreeDsl.tree
import com.github.aivanovski.testswithme.entity.Bounds
import com.github.aivanovski.testswithme.entity.UiEntity
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.splitIntoLines

import org.junit.jupiter.api.Test

class PhoneScreenNodeFormatterTest {

    @Test
    fun format() {
        val tree = buildTreeWithDsl()

        val result = AsciScreenNodeFormatter(1080, 2400, 14 * 4, 32).format(tree)

        println("----------------------------")
        for (line in result.splitIntoLines()) println(line)
        println("----------------------------")
    }

    private fun buildTreeWithDsl(): UiNode<Unit> {
        val root = newNode()

        val t1 = newNode(
            text = "Server url",
            bounds = Bounds(42, 349, 230, 406)
        )

        val t2 = newNode(
            text = "Driver Gateway",
            bounds = Bounds(42, 1214, 336, 1271)
        )

        val t3 = newNode(
            text = "Enable this to start the Drive",
            bounds = Bounds(
                42, 1271, 885, 1534
            )
        )

        val t4 = newNode(
            text = "Flakiness Configuration",
            bounds = Bounds(42, 1591, 1038, 1648)
        )

        val t5 = newNode(
            text = "Configure settings to handle",
            bounds = Bounds(42, 1648, 1038, 1805)
        )

        return tree(root) {
            node(t1)
            node(t2)
            node(t3)
            node(t4)
            node(t5)
        }
    }

    private fun newNode(
        className: String? = null,
        text: String? = null,
        contentDescription: String? = null,
        bounds: Bounds? = null,
        isEnabled: Boolean? = null,
        isEditable: Boolean? = null,
        isFocused: Boolean? = null,
        isFocusable: Boolean? = null,
        isClickable: Boolean? = null,
        isLongClickable: Boolean? = null,
        isCheckable: Boolean? = null,
        isChecked: Boolean? = null
    ): UiNode<Unit> =
        UiNode(
            source = Unit,
            entity = UiEntity(
                resourceId = null,
                packageName = "com.example.application",
                className = className,
                bounds = bounds,
                text = text,
                contentDescription = contentDescription,
                isEnabled = isEnabled,
                isEditable = isEditable,
                isFocused = isFocused,
                isFocusable = isFocusable,
                isClickable = isClickable,
                isLongClickable = isLongClickable,
                isCheckable = isCheckable,
                isChecked = isChecked
            ),
            nodes = mutableListOf()
        )

    /*
    View
      View
        View
          View
            TextView [text=Server url, bounds=[42,349:230,406]]
            TextView [text=https://testswithme.org, bounds=[42,406:436,455]]
          TextView [text=Validate SSL certificates, bounds=[42,560:519,617]]
          TextView [text=Requires application restart, bounds=[42,617:501,666]]
          TextView [text=Test Driver, bounds=[42,764:1038,821]]
          TextView [text=Enable this to run tests usin…, bounds=[42,821:1038,1031]]
          TextView [text=Driver is RUNNING, bounds=[42,1077:771,1134]]
          View
            TextView [text=Settings, bounds=[813,1077:975,1134]]
          TextView [text=Driver Gateway, bounds=[42,1214:336,1271]]
          TextView [text=Enable this to start the Drive…, bounds=[42,1271:885,1534]]
          TextView [text=Flakiness Configuration, bounds=[42,1591:1038,1648]]
          TextView [text=Configure settings to handle f…, bounds=[42,1648:1038,1805]]
          View
            TextView [text=Delay Scale Factor, bounds=[42,1869:401,1926]]
            TextView [text=1x, bounds=[42,1926:83,1975]]
          View
            TextView [text=Number of Retries, bounds=[42,2080:397,2137]]
            TextView [text=3, bounds=[42,2137:64,2186]]
      View
        TextView [text=Settings, bounds=[148,173:356,250]]
     */
}