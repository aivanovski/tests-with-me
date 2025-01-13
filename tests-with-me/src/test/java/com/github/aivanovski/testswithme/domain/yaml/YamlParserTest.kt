package com.github.aivanovski.testswithme.domain.yaml

import arrow.core.Either
import com.github.aivanovski.testswithme.entity.ConditionType
import com.github.aivanovski.testswithme.entity.Duration
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.FlowStepPrecondition
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.entity.UiElementSelector
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.entity.exception.ParsingException
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.flow.yaml.YamlParser.Companion.KEY_BACK
import com.github.aivanovski.testswithme.flow.yaml.YamlParser.Companion.KEY_HOME
import com.github.aivanovski.testswithme.flow.yaml.model.TextLineRange
import com.github.aivanovski.testswithme.utils.StringUtils
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class YamlParserTest {

    @Test
    fun `should skip comments`() {
        val content = """
            - launch: $PACKAGE_NAME
            # Commented text
            - launch: $PACKAGE_NAME # Commented text
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = listOf(
                FlowStep.Launch(PACKAGE_NAME),
                FlowStep.Launch(PACKAGE_NAME)
            )
        )
    }

    @Test
    fun `should parse pressKey`() {
        val content = """
            - pressKey: $KEY_BACK
            - pressKey: $KEY_HOME
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = PRESS_KEY_STEPS
        )
    }

    @Test
    fun `should parse launch`() {
        val content = """
            - launch: $PACKAGE_NAME
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = LAUNCH_STEPS
        )
    }

    @Test
    fun `should parse sendBroadcast`() {
        val content = """
            - sendBroadcast: $PACKAGE_NAME/$BROADCAST_ACTION
            - sendBroadcast:
                name: $PACKAGE_NAME/$BROADCAST_ACTION
            - sendBroadcast:
                name: $PACKAGE_NAME/$BROADCAST_ACTION
                data:
                  - key: $BROADCAST_KEY
                    value: $BROADCAST_VALUE
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = SEND_BROADCAST_STEPS
        )
    }

    @Test
    fun `should parse sendBroadcast with conditions`() {
        val content = """
            - sendBroadcast:
                name: $PACKAGE_NAME/$BROADCAST_ACTION
                when:
                  visible: $TEXT
            - sendBroadcast:
                name: $PACKAGE_NAME/$BROADCAST_ACTION
                when:
                  notVisible: $TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = SEND_BROADCAST_WITH_Predicate_STEPS
        )
    }

    @Test
    fun `should parse tapOn`() {
        val content = """
            - tapOn: $TEXT
            - tapOn:
                text: $TEXT
            - tapOn:
                hasText: $HAS_TEXT
            - tapOn:
                contentDescription: $CONTENT_DESCRIPTION
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = TAP_ON_STEPS
        )
    }

    @Test
    fun `should parse tapOn with conditions`() {
        val content = """
            - tapOn: $TEXT
                when:
                  visible: $VISIBLE_TEXT
            - tapOn: $TEXT
                when:
                  visible:
                    text: $VISIBLE_TEXT
            - tapOn: $TEXT
                when:
                  visible:
                    hasText: $HAS_TEXT
            - tapOn: $TEXT
                when:
                  visible:
                    contentDescription: $CONTENT_DESCRIPTION

            - tapOn: $TEXT
                when:
                  notVisible: $NOT_VISIBLE_TEXT
            - tapOn: $TEXT
                when:
                  notVisible:
                    text: $NOT_VISIBLE_TEXT
            - tapOn: $TEXT
                when:
                  notVisible:
                    hasText: $HAS_TEXT
            - tapOn: $TEXT
                when:
                  notVisible:
                    contentDescription: $CONTENT_DESCRIPTION
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = TAP_ON_WITH_Predicate_STEP
        )
    }

    @Test
    fun `should parse assertVisible`() {
        val content = """
            - assertVisible: $TEXT
            - assertVisible: [$TEXT1, $TEXT2, $TEXT3]
            - assertVisible:
                text: $TEXT
            - assertVisible:
                contentDescription: $CONTENT_DESCRIPTION
            - assertVisible:
                hasText: $HAS_TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = ASSERT_VISIBLE_STEPS
        )
    }

    @Test
    fun `should parse assertNotVisible`() {
        val content = """
            - assertNotVisible: $TEXT
            - assertNotVisible: [$TEXT1, $TEXT2, $TEXT3]
            - assertNotVisible:
                text: $TEXT
            - assertNotVisible:
                contentDescription: $CONTENT_DESCRIPTION
            - assertNotVisible:
                hasText: $HAS_TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = ASSERT_NOT_VISIBLE_STEPS
        )
    }

    @Test
    fun `should parse name`() {
        val content = """
            - name: $NAME
        """.trimIndent()

        parse(content) shouldBe Either.Right(newYamlFlow(name = NAME))
    }

    @Test
    fun `should parse runFlow`() {
        val content = """
            - runFlow: $PROJECT / $NAME
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = RUN_FLOW_STEPS
        )
    }

    @Test
    fun `should parse runFlow with conditions`() {
        val content = """
            - runFlow: $PROJECT / $NAME
                when:
                  visible: $TEXT
            - runFlow: $PROJECT / $NAME
                when:
                  notVisible: $TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = RUN_FLOW_WITH_Predicate_STEPS
        )
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
                hasText: $HAS_TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = INPUT_TEXT_STEPS
        )
    }

    @Test
    fun `should parse inputText with conditions`() {
        val content = """
            - inputText: $INPUT_TEXT
                when:
                  visible: $TEXT
            - inputText: $INPUT_TEXT
                when:
                  notVisible: $TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = INPUT_TEXT_WITH_Predicate_STEPS
        )
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
                hasText: $HAS_TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = LONG_TAP_ON_STEPS
        )
    }

    @Test
    fun `should parse longTapOn with conditions`() {
        val content = """
            - longTapOn: $TEXT
                when:
                  visible: $VISIBLE_TEXT
            - longTapOn: $TEXT
                when:
                  notVisible: $NOT_VISIBLE_TEXT
        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = LONG_TAP_ON_WITH_Predicate_STEPS
        )
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
    fun `should parse waitUntil`() {
        val content = """
            - waitUntil:
                visible: $TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS
            - waitUntil:
                visible:
                  text: $TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS
            - waitUntil:
                visible:
                  hasText: $HAS_TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS
            - waitUntil:
                visible:
                  contentDescription: $CONTENT_DESCRIPTION
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS

            - waitUntil:
                notVisible: $TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS
            - waitUntil:
                notVisible:
                  text: $TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS
            - waitUntil:
                notVisible:
                  hasText: $HAS_TEXT
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS
            - waitUntil:
                notVisible:
                  contentDescription: $CONTENT_DESCRIPTION
                step: $DURATION_MILLISECONDS
                timeout: $DURATION_SECONDS

        """.trimIndent()

        assertParsedSteps(
            content = content,
            expectedSteps = WAIT_UNTIL_STEPS
        )
    }

    private fun assertParsedSteps(
        content: String,
        expectedSteps: List<FlowStep>,
        expectedLineNumbers: List<IntRange>? = null
    ) {
        val result = parse(content)

        if (result.isLeft()) {
            result.unwrapError().printStackTrace()
        }

        result.isRight() shouldBe true

        result.unwrap().steps shouldBe expectedSteps

        if (expectedLineNumbers != null) {
            val numbers = result.unwrap().stepLines
                .map { range -> range.start..range.end }

            numbers shouldBe expectedLineNumbers
        }
    }

    private fun parse(content: String): Either<ParsingException, YamlFlow> {
        return YamlParser().parse(content)
    }

    private fun newYamlFlow(
        name: String = StringUtils.EMPTY,
        project: String? = null,
        group: String? = null,
        steps: List<FlowStep> = emptyList(),
        stepLines: List<TextLineRange> = emptyList()
    ): YamlFlow {
        return YamlFlow(
            name = name,
            project = project,
            group = group,
            steps = steps,
            stepLines = stepLines
        )
    }

    object Selectors {
        val TEXT = UiElementSelector.text(Strings.TEXT)
        val HAS_TEXT = UiElementSelector.containsText(Strings.HAS_TEXT)
        val CONTENT_DESCRIPTION = UiElementSelector.contentDescription(Strings.CONTENT_DESCRIPTION)
        val VISIBLE_TEXT = UiElementSelector.text(Strings.VISIBLE_TEXT)
        val NOT_VISIBLE_TEXT = UiElementSelector.text(Strings.NOT_VISIBLE_TEXT)
    }

    object Strings {
        const val TEXT = "text"
        const val HAS_TEXT = "has-text"
        const val CONTENT_DESCRIPTION = "content-description"
        const val VISIBLE_TEXT = "visible-text"
        const val NOT_VISIBLE_TEXT = "not-visible-text"
    }

    companion object {
        private const val DURATION_SECONDS = "10"
        private const val DURATION_MILLISECONDS = "123"
        private const val NAME = "flow-name"
        private const val PROJECT = "project-name"
        private const val GROUP = "group"
        private const val TEXT = Strings.TEXT
        private const val TEXT1 = "text1"
        private const val TEXT2 = "text2"
        private const val TEXT3 = "text3"
        private const val VISIBLE_TEXT = Strings.VISIBLE_TEXT
        private const val NOT_VISIBLE_TEXT = Strings.NOT_VISIBLE_TEXT
        private const val CONTENT_DESCRIPTION = Strings.CONTENT_DESCRIPTION
        private const val HAS_TEXT = Strings.HAS_TEXT
        private const val INPUT_TEXT = "input-text"
        private const val PACKAGE_NAME = "com.android.app"
        private const val BROADCAST_ACTION = "com.android.app.BroadcastReceiver"
        private const val BROADCAST_KEY = "broadcast-key"
        private const val BROADCAST_VALUE = "broadcast-value"

        private fun String.toDuration(): Duration {
            val value = this.toInt()
            return if (value >= 100) Duration.millis(value) else Duration.seconds(value)
        }

        private fun UiElementSelector.asList(): List<UiElementSelector> {
            return listOf(this)
        }

        private val SEND_BROADCAST_STEPS = listOf(
            FlowStep.SendBroadcast(
                packageName = PACKAGE_NAME,
                action = BROADCAST_ACTION,
                data = emptyMap()
            ),
            FlowStep.SendBroadcast(
                packageName = PACKAGE_NAME,
                action = BROADCAST_ACTION,
                data = emptyMap()
            ),
            FlowStep.SendBroadcast(
                packageName = PACKAGE_NAME,
                action = BROADCAST_ACTION,
                data = mapOf(BROADCAST_KEY to BROADCAST_VALUE)
            )
        )

        private val SEND_BROADCAST_WITH_Predicate_STEPS = listOf(
            FlowStep.SendBroadcast(
                packageName = PACKAGE_NAME,
                action = BROADCAST_ACTION,
                data = emptyMap(),
                condition = FlowStepPrecondition(ConditionType.VISIBLE, Selectors.TEXT)
            ),
            FlowStep.SendBroadcast(
                packageName = PACKAGE_NAME,
                action = BROADCAST_ACTION,
                data = emptyMap(),
                condition = FlowStepPrecondition(ConditionType.NOT_VISIBLE, Selectors.TEXT)
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
                elements = listOf(
                    UiElementSelector.text(TEXT1),
                    UiElementSelector.text(TEXT2),
                    UiElementSelector.text(TEXT3)
                )
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertVisible(
                elements = UiElementSelector.contentDescription(CONTENT_DESCRIPTION).asList()
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
                elements = listOf(
                    UiElementSelector.text(TEXT1),
                    UiElementSelector.text(TEXT2),
                    UiElementSelector.text(TEXT3)
                )
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.text(TEXT).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.contentDescription(CONTENT_DESCRIPTION).asList()
            ),
            FlowStep.AssertNotVisible(
                elements = UiElementSelector.containsText(HAS_TEXT).asList()
            )
        )

        private val TAP_ON_STEPS = listOf(
            FlowStep.TapOn(Selectors.TEXT),
            FlowStep.TapOn(Selectors.TEXT),
            FlowStep.TapOn(Selectors.HAS_TEXT),
            FlowStep.TapOn(Selectors.CONTENT_DESCRIPTION)
        )

        private val TAP_ON_WITH_Predicate_STEP = listOf(
            // when visible
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(ConditionType.VISIBLE, Selectors.VISIBLE_TEXT)
            ),
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(ConditionType.VISIBLE, Selectors.VISIBLE_TEXT)
            ),
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(ConditionType.VISIBLE, Selectors.HAS_TEXT)
            ),
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(
                    ConditionType.VISIBLE,
                    Selectors.CONTENT_DESCRIPTION
                )
            ),

            // when notVisible
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(
                    ConditionType.NOT_VISIBLE,
                    Selectors.NOT_VISIBLE_TEXT
                )
            ),
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(
                    ConditionType.NOT_VISIBLE,
                    Selectors.NOT_VISIBLE_TEXT
                )
            ),
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(ConditionType.NOT_VISIBLE, Selectors.HAS_TEXT)
            ),
            FlowStep.TapOn(
                element = Selectors.TEXT,
                condition = FlowStepPrecondition(
                    ConditionType.NOT_VISIBLE,
                    Selectors.CONTENT_DESCRIPTION
                )
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
                element = UiElementSelector.containsText(HAS_TEXT),
                isLong = true
            )
        )

        private val LONG_TAP_ON_WITH_Predicate_STEPS = listOf(
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT),
                isLong = true,
                condition = FlowStepPrecondition(ConditionType.VISIBLE, Selectors.VISIBLE_TEXT)
            ),
            FlowStep.TapOn(
                element = UiElementSelector.text(TEXT),
                isLong = true,
                condition = FlowStepPrecondition(
                    ConditionType.NOT_VISIBLE,
                    Selectors.NOT_VISIBLE_TEXT
                )
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
                element = UiElementSelector.containsText(HAS_TEXT)
            )
        )

        private val INPUT_TEXT_WITH_Predicate_STEPS = listOf(
            FlowStep.InputText(
                text = INPUT_TEXT,
                condition = FlowStepPrecondition(ConditionType.VISIBLE, Selectors.TEXT)
            ),
            FlowStep.InputText(
                text = INPUT_TEXT,
                condition = FlowStepPrecondition(ConditionType.NOT_VISIBLE, Selectors.TEXT)
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
                conditionType = ConditionType.VISIBLE,
                element = Selectors.TEXT,
                step = DURATION_MILLISECONDS.toDuration(),
                timeout = DURATION_SECONDS.toDuration()
            ),
            FlowStep.WaitUntil(
                conditionType = ConditionType.VISIBLE,
                element = Selectors.TEXT,
                step = DURATION_MILLISECONDS.toDuration(),
                timeout = DURATION_SECONDS.toDuration()
            ),
            FlowStep.WaitUntil(
                conditionType = ConditionType.VISIBLE,
                element = Selectors.HAS_TEXT,
                step = DURATION_MILLISECONDS.toDuration(),
                timeout = DURATION_SECONDS.toDuration()
            ),
            FlowStep.WaitUntil(
                conditionType = ConditionType.VISIBLE,
                element = Selectors.CONTENT_DESCRIPTION,
                step = DURATION_MILLISECONDS.toDuration(),
                timeout = DURATION_SECONDS.toDuration()
            ),

            FlowStep.WaitUntil(
                conditionType = ConditionType.NOT_VISIBLE,
                element = Selectors.TEXT,
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            ),
            FlowStep.WaitUntil(
                conditionType = ConditionType.NOT_VISIBLE,
                element = Selectors.TEXT,
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            ),
            FlowStep.WaitUntil(
                conditionType = ConditionType.NOT_VISIBLE,
                element = Selectors.HAS_TEXT,
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            ),
            FlowStep.WaitUntil(
                conditionType = ConditionType.NOT_VISIBLE,
                element = Selectors.CONTENT_DESCRIPTION,
                step = Duration.millis(DURATION_MILLISECONDS.toInt()),
                timeout = Duration.seconds(DURATION_SECONDS.toInt())
            )
        )

        private val RUN_FLOW_STEPS = listOf(
            FlowStep.RunFlow(
                path = "$PROJECT / $NAME"
            )
        )

        private val RUN_FLOW_WITH_Predicate_STEPS = listOf(
            FlowStep.RunFlow(
                path = "$PROJECT / $NAME",
                condition = FlowStepPrecondition(ConditionType.VISIBLE, Selectors.TEXT)
            ),
            FlowStep.RunFlow(
                path = "$PROJECT / $NAME",
                condition = FlowStepPrecondition(ConditionType.NOT_VISIBLE, Selectors.TEXT)
            )
        )
    }
}