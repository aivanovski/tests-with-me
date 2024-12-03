package com.github.aivanovski.testswithme.domain.yaml

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.Duration
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.entity.exception.ParsingException
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.flow.yaml.YamlParser.Companion.KEY_BACK
import com.github.aivanovski.testswithme.flow.yaml.YamlParser.Companion.KEY_HOME
import com.github.aivanovski.testswithme.utils.StringUtils
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class YamlParserTest {

    @Test
    fun `should parse assertVisible`() {
        val content = """
            - assertVisible: $TEXT

            - assertVisible:
                text: $TEXT

            - assertVisible:
                contentDescription: $CONTENT_DESCRIPTION

            - assertVisible:
                id: $ID

            - assertVisible:
                hasText: $HAS_TEXT
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = ASSERT_VISIBLE_STEPS))
    }

    @Test
    fun `should parse assertNotVisible`() {
        val content = """
            - assertNotVisible: $TEXT

            - assertNotVisible:
                text: $TEXT

            - assertNotVisible:
                contentDescription: $CONTENT_DESCRIPTION

            - assertNotVisible:
                id: $ID

            - assertNotVisible:
                hasText: $HAS_TEXT
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = ASSERT_NOT_VISIBLE_STEPS))
    }

    @Test
    fun `should parse tapOn`() {
        val content = """
            - tapOn: $TEXT

            - tapOn:
                text: $TEXT

            - tapOn:
                contentDescription: $CONTENT_DESCRIPTION

            - tapOn:
                id: $ID

            - tapOn:
                hasText: $HAS_TEXT
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = TAP_ON_STEPS))
    }

    @Test
    fun `should parse longTapOn`() {
        val content = """
            - longTapOn: $TEXT

            - longTapOn:
                text: $TEXT

            - longTapOn:
                contentDescription: $CONTENT_DESCRIPTION

            - longTapOn:
                id: $ID

            - longTapOn:
                hasText: $HAS_TEXT
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = LONG_TAP_ON_STEPS))
    }

    @Test
    fun `should parse inputText`() {
        val content = """
            - inputText: $INPUT_TEXT

            - inputText:
                input: $INPUT_TEXT
                text: $TEXT

            - inputText:
                input: $INPUT_TEXT
                contentDescription: $CONTENT_DESCRIPTION

            - inputText:
                input: $INPUT_TEXT
                id: $ID

            - inputText:
                input: $INPUT_TEXT
                hasText: $HAS_TEXT
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = INPUT_TEXT_STEPS))
    }

    @Test
    fun `should parse launch`() {
        val content = """
            - launch: $PACKAGE_NAME
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = LAUNCH_STEPS))
    }

    @Test
    fun `should parse sendBroadcast`() {
        val content = """
            - sendBroadcast: $PACKAGE_NAME/$BROADCAST_ACTION
              data:
                - key: $BROADCAST_KEY
                  value: $BROADCAST_VALUE
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = SEND_BROADCAST_STEPS))
    }

    @Test
    fun `should parse name`() {
        val content = """
            - name: $NAME
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(name = NAME))
    }

    @Test
    fun `should parse project`() {
        val content = """
            - project: $PROJECT
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(project = PROJECT))
    }

    @Test
    fun `should parse group`() {
        val content = """
            - group: $GROUP
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(group = GROUP))
    }

    @Test
    fun `should parse pressKey`() {
        val content = """
            - pressKey: $KEY_BACK
            - pressKey: $KEY_HOME
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = PRESS_KEY_STEPS))
    }

    @Test
    fun `should parse waitUntil`() {
        val content = """
            - waitUntil:
                text: $TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS

            - waitUntil:
                contentDescription: $CONTENT_DESCRIPTION
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS

            - waitUntil:
                id: $ID
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS

            - waitUntil:
                hasText: $HAS_TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = WAIT_UNTIL_STEPS))
    }

    @Test
    fun `should parse runFlow`() {
        val content = """
            - runFlow: $PROJECT / $NAME
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(steps = RUN_FLOW_STEPS))
    }

    private fun parse(
        content: String
    ): Either<ParsingException, YamlFlow> {
        return YamlParser().parse(content)
    }

    private fun newYamlFlow(
        name: String = StringUtils.EMPTY,
        project: String? = null,
        group: String? = null,
        steps: List<FlowStep> = emptyList()
    ): YamlFlow {
        return YamlFlow(
            name = name,
            project = project,
            group = group,
            steps = steps
        )
    }

    companion object {
        private const val DURATION_SECONDS = "10"
        private const val DURATION_MILLISECONDS = "123"
        private const val NAME = "flow-name"
        private const val PROJECT = "project-name"
        private const val GROUP = "group"
        private const val TEXT = "element-text"
        private const val ID = "element-id"
        private const val CONTENT_DESCRIPTION = "content-description"
        private const val HAS_TEXT = "has-text"
        private const val INPUT_TEXT = "input-text"
        private const val PACKAGE_NAME = "com.android.app"
        private const val BROADCAST_ACTION = "com.android.app.BroadcastReceiver"
        private const val BROADCAST_KEY = "broadcast-key"
        private const val BROADCAST_VALUE = "broadcast-value"

        private fun UiElementSelector.asList(): List<UiElementSelector> {
            return listOf(this)
        }

        private val SEND_BROADCAST_STEPS = listOf(
            FlowStep.SendBroadcast(
                packageName = PACKAGE_NAME,
                action = BROADCAST_ACTION,
                data = mapOf(BROADCAST_KEY to BROADCAST_VALUE)
            )
        )

        private val LAUNCH_STEPS = listOf(
            FlowStep.Launch(PACKAGE_NAME)
        )

        private val ASSERT_VISIBLE_STEPS = listOf(
            FlowStep.AssertVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.contentDescription(CONTENT_DESCRIPTION).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.id(ID).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.containsText(HAS_TEXT).asList()
            )
        )

        private val ASSERT_NOT_VISIBLE_STEPS = listOf(
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.contentDescription(CONTENT_DESCRIPTION).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.id(ID).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.containsText(HAS_TEXT).asList()
            )
        )

        private val TAP_ON_STEPS = listOf(
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.contentDescription(CONTENT_DESCRIPTION)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.id(ID)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.containsText(HAS_TEXT)
            )
        )

        private val LONG_TAP_ON_STEPS = listOf(
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.contentDescription(CONTENT_DESCRIPTION),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.id(ID),
                isLong = true
            ),
            FlowStep.TapOn(
                element = UiElementSelector.containsText(HAS_TEXT),
                isLong = true
            )
        )

        private val INPUT_TEXT_STEPS = listOf(
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = null
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.text(TEXT)
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.contentDescription(CONTENT_DESCRIPTION)
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.id(ID)
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                element = UiElementSelector.containsText(HAS_TEXT)
            )
        )

        private val PRESS_KEY_STEPS = listOf(
            FlowStep.PressKey(
                key = KeyCode.Back
            ),
            FlowStep.PressKey(
                key = KeyCode.Home
            )
        )

        private val WAIT_UNTIL_STEPS = listOf(
            FlowStep.WaitUntil(
                element = UiElementSelector.text(TEXT),
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            ),
            FlowStep.WaitUntil(
                element = UiElementSelector.contentDescription(CONTENT_DESCRIPTION),
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            ),
            FlowStep.WaitUntil(
                element = UiElementSelector.id(ID),
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            ),
            FlowStep.WaitUntil(
                element = UiElementSelector.containsText(HAS_TEXT),
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            )
        )

        private val RUN_FLOW_STEPS = listOf(
            FlowStep.RunFlow(
                name = "$PROJECT / $NAME"
            )
        )
    }
}