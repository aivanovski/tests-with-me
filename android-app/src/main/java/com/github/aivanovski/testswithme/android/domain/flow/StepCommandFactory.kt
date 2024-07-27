package com.github.aivanovski.testswithme.android.domain.flow

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.flow.commands.Assert
import com.github.aivanovski.testswithme.flow.commands.Broadcast
import com.github.aivanovski.testswithme.flow.commands.ExecutableStepCommand
import com.github.aivanovski.testswithme.flow.commands.InputText
import com.github.aivanovski.testswithme.flow.commands.Launch
import com.github.aivanovski.testswithme.flow.commands.PressKey
import com.github.aivanovski.testswithme.flow.commands.RunFlow
import com.github.aivanovski.testswithme.flow.commands.StepCommand
import com.github.aivanovski.testswithme.flow.commands.Tap
import com.github.aivanovski.testswithme.flow.commands.WaitUntil
import com.github.aivanovski.testswithme.flow.commands.assertion.NotVisibleAssertion
import com.github.aivanovski.testswithme.flow.commands.assertion.VisibleAssertion

class StepCommandFactory(
    private val interactor: FlowRunnerInteractor
) {

    suspend fun createCommand(
        flow: FlowEntry,
        step: FlowStep
    ): Either<AppException, StepCommand> =
        either {
            when (step) {
                is FlowStep.SendBroadcast ->
                    Broadcast(
                        packageName = step.packageName,
                        action = step.action,
                        data = step.data
                    )

                is FlowStep.Launch ->
                    Launch(
                        packageName = step.packageName
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

                is FlowStep.RunFlow -> createRunFlowCommand(
                    parent = flow,
                    step = step
                ).bind()
            }
        }

    private suspend fun createRunFlowCommand(
        parent: FlowEntry,
        step: FlowStep.RunFlow
    ): Either<AppException, StepCommand> =
        either {
            val nameOrUid = step.name

            val flow = findFlowByNameOrUid(
                parent = parent,
                nameOrUid = nameOrUid
            ).bind()

            val commands = mutableListOf<ExecutableStepCommand<Any>>()
            for (innerStep in flow.steps) {
                val innerCommand = createCommand(
                    flow = parent,
                    step = innerStep.command
                ).bind()
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

    private suspend fun findFlowByNameOrUid(
        parent: FlowEntry,
        nameOrUid: String
    ): Either<AppException, FlowWithSteps> =
        either {
            val getByUidResult = interactor.getFlowByUid(nameOrUid)
            if (getByUidResult.isRight()) {
                return@either getByUidResult.bind()
            }

            val values = nameOrUid
                .split("/")
                .map { value -> value.trim() }
                .filter { value -> value.isNotEmpty() }

            if (values.isEmpty()) {
                raise(AppException("Unable to find flow: $nameOrUid"))
            }

            val flowName = values.last()
            val groupName = if (values.size > 1) {
                values[values.size - 2]
            } else {
                null
            }

            interactor.findFlowByName(
                projectUid = parent.projectUid,
                groupName = groupName,
                name = flowName
            ).bind()
                ?: raise(AppException("Unable to find flow: $nameOrUid"))
        }
}