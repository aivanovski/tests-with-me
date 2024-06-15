package com.github.aivanovski.testwithme.android.domain.flow

import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.domain.FlowInteractor
import com.github.aivanovski.testwithme.flow.commands.Assert
import com.github.aivanovski.testwithme.flow.commands.Broadcast
import com.github.aivanovski.testwithme.flow.commands.InputText
import com.github.aivanovski.testwithme.flow.commands.Launch
import com.github.aivanovski.testwithme.flow.commands.PressKey
import com.github.aivanovski.testwithme.flow.commands.RunFlow
import com.github.aivanovski.testwithme.flow.commands.Tap
import com.github.aivanovski.testwithme.flow.commands.WaitUntil
import com.github.aivanovski.testwithme.flow.commands.assertion.NotVisibleAssertion
import com.github.aivanovski.testwithme.flow.commands.assertion.VisibleAssertion
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.flow.commands.ExecutableStepCommand
import com.github.aivanovski.testwithme.flow.commands.StepCommand
import com.github.aivanovski.testwithme.entity.FlowStep
import arrow.core.Either

class StepCommandFactory(
    private val interactor: FlowInteractor
) {

    suspend fun createCommand(
        step: FlowStep
    ): Either<AppException, StepCommand> = either {
        when (step) {
            is FlowStep.SendBroadcast ->
                Broadcast(
                    packageName = step.packageName,
                    action = step.action,
                    data = step.data
                )

            is FlowStep.Launch ->
                Launch(
                    packageName = step.packageName,
                )

            is FlowStep.AssertVisible ->
                Assert(
                    parent = null,
                    elements = step.elements,
                    assertion = VisibleAssertion()
                )

            is FlowStep.AssertNotVisible ->
                Assert(
                    parent = null,
                    elements = step.elements,
                    assertion = NotVisibleAssertion()
                )

            is FlowStep.TapOn ->
                Tap(
                    element = step.element,
                    isLongTap = step.isLong
                )

            is FlowStep.InputText ->
                InputText(
                    text = step.text,
                    element = step.element
                )

            is FlowStep.PressKey ->
                PressKey(
                    key = step.key
                )

            is FlowStep.WaitUntil ->
                WaitUntil(
                    element = step.element,
                    step = step.step,
                    timeout = step.timeout
                )

            is FlowStep.RunFlow -> createRunFlowCommand(step).bind()
        }
    }

    private suspend fun createRunFlowCommand(
        step: FlowStep.RunFlow
    ): Either<AppException, StepCommand> = either {
        val flowUid = step.flowUid

        val flow = interactor.getFlowByUid(flowUid).bind()

        val commands = mutableListOf<ExecutableStepCommand<Any>>()
        for (innerStep in flow.steps) {
            val innerCommand = createCommand(innerStep.command).bind()
            if (innerCommand !is ExecutableStepCommand<*>) {
                raise(AppException("Unsupported command: ${innerStep.command}"))
            }

            commands.add(innerCommand)
        }

        RunFlow(
            flowUid = flow.entry.uid,
            name = flow.entry.name,
            commands = commands
        )
    }
}