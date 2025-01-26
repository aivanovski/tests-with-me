package com.github.aivanovski.testswithme.flow.runner.report

import arrow.core.Either
import arrow.core.right
import com.github.aivanovski.testswithme.flow.runner.report.model.ReportItem
import com.github.aivanovski.testswithme.flow.runner.report.model.ReportItem.FlowItem
import com.github.aivanovski.testswithme.flow.runner.report.model.exception.ReportParsingException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ReportParserTest {

    @Test
    fun `parse should work`() {
        parse(REPORT) shouldBe EXPECTED.right()
    }

    private fun parse(content: String): Either<ReportParsingException, FlowItem> =
        ReportParser().parse(content)

    companion object {

        private val STACKTRACE = """
            com.github.aivanovski.testswithme.entity.exception.FlowExecutionException: Elements should be visible: [text = Basic entry]
                at com.github.aivanovski.testswithme.entity.exception.FlowExecutionException.Companion.fromFlowError(FlowExecutionException.kt:13)
                at com.github.aivanovski.testswithme.android.domain.flow.CommandExecutor.transformError(CommandExecutor.kt:50)
                at com.github.aivanovski.testswithme.android.domain.flow.CommandExecutor.execute(CommandExecutor.kt:71)
                at com.github.aivanovski.testswithme.android.domain.flow.FlowRunner.runCurrentStep(FlowRunner.kt:246)
                at com.github.aivanovski.testswithme.android.domain.flow.FlowRunner.access.runCurrentStep(FlowRunner.kt:38)
                at com.github.aivanovski.testswithme.android.domain.flow.FlowRunner.runCurrentStep.1.invokeSuspend(Unknown Source:17)
                at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
                at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:104)
                at android.os.Handler.handleCallback(Handler.java:958)
                at android.os.Handler.dispatchMessage(Handler.java:99)
                at android.os.Looper.loopOnce(Looper.java:205)
                at android.os.Looper.loop(Looper.java:294)
                at android.app.ActivityThread.main(ActivityThread.java:8177)
                at java.lang.reflect.Method.invoke(Native Method)
                at com.android.internal.os.RuntimeInit.MethodAndArgsCaller.run(RuntimeInit.java:552)
                at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:971)
        """.trimIndent()

        private val EXPECTED = FlowItem(
            name = "Parent flow",
            steps = listOf(
                FlowItem(
                    name = "Child flow",
                    steps = listOf(
                        ReportItem.StepItem(
                            step = "Broadcast: packageName/action [key=value]",
                            attemptCount = 1,
                            isSuccess = true
                        )
                    ),
                    isSuccess = true
                ),
                ReportItem.StepItem(
                    step = "Launch app: package name = com.application",
                    attemptCount = 1,
                    isSuccess = true
                ),
                ReportItem.StepItem(
                    step = "Assert is visible: [text = someText]",
                    attemptCount = 1,
                    isSuccess = true
                ),
                ReportItem.StepItem(
                    step = "Assert is visible: [text = Basic entry]",
                    attemptCount = 3,
                    isSuccess = false,
                    error = "Elements should be visible: [text = Basic entry]"
                )
            ),
            isSuccess = false,
            error = "Elements should be visible: [text = Basic entry]",
            stacktrace = STACKTRACE
        )

        private val REPORT = """
            Start flow 'Parent flow'
            [Parent flow] Step 1: Run flow 'Child flow'
            Start flow 'Child flow'
            [Child flow] Step 1: Broadcast: packageName/action [key=value]
            [Child flow] Step 1: SUCCESS
            Flow 'Child flow' finished successfully
            [Parent flow] Step 1: SUCCESS
            [Parent flow] Step 2: Launch app: package name = com.application
            [Parent flow] Step 2: SUCCESS
            [Parent flow] Step 3: Assert is visible: [text = someText]
            [Parent flow] Step 3: SUCCESS
            [Parent flow] Step 4: Assert is visible: [text = Basic entry]
            [Parent flow] Step 4: FAILED, Elements should be visible: [text = Basic entry]
            [Parent flow] Retry 4: Assert is visible: [text = Basic entry]
            [Parent flow] Step 4: FAILED, Elements should be visible: [text = Basic entry]
            [Parent flow] Retry 4: Assert is visible: [text = Basic entry]
            [Parent flow] Step 4: FAILED, Elements should be visible: [text = Basic entry]
            Flow 'Parent flow' failed: Elements should be visible: [text = Basic entry]
            com.github.aivanovski.testswithme.entity.exception.FlowExecutionException: Elements should be visible: [text = Basic entry]
                at com.github.aivanovski.testswithme.entity.exception.FlowExecutionException.Companion.fromFlowError(FlowExecutionException.kt:13)
                at com.github.aivanovski.testswithme.android.domain.flow.CommandExecutor.transformError(CommandExecutor.kt:50)
                at com.github.aivanovski.testswithme.android.domain.flow.CommandExecutor.execute(CommandExecutor.kt:71)
                at com.github.aivanovski.testswithme.android.domain.flow.FlowRunner.runCurrentStep(FlowRunner.kt:246)
                at com.github.aivanovski.testswithme.android.domain.flow.FlowRunner.access.runCurrentStep(FlowRunner.kt:38)
                at com.github.aivanovski.testswithme.android.domain.flow.FlowRunner.runCurrentStep.1.invokeSuspend(Unknown Source:17)
                at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
                at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:104)
                at android.os.Handler.handleCallback(Handler.java:958)
                at android.os.Handler.dispatchMessage(Handler.java:99)
                at android.os.Looper.loopOnce(Looper.java:205)
                at android.os.Looper.loop(Looper.java:294)
                at android.app.ActivityThread.main(ActivityThread.java:8177)
                at java.lang.reflect.Method.invoke(Native Method)
                at com.android.internal.os.RuntimeInit.MethodAndArgsCaller.run(RuntimeInit.java:552)
                at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:971)
        """.trimIndent()
    }
}