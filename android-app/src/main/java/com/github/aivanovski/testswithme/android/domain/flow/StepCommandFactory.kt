package com.github.aivanovski.testswithme.android.domain.flow

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.entity.ConditionType
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.flow.commands.Assert
import com.github.aivanovski.testswithme.flow.commands.Broadcast
import com.github.aivanovski.testswithme.flow.commands.ExecutableStepCommand
import com.github.aivanovski.testswithme.flow.commands.InputText
import com.github.aivanovski.testswithme.flow.commands.Launch
import com.github.aivanovski.testswithme.flow.commands.Precondition
import com.github.aivanovski.testswithme.flow.commands.PressKey
import com.github.aivanovski.testswithme.flow.commands.RunFlow
import com.github.aivanovski.testswithme.flow.commands.StepCommand
import com.github.aivanovski.testswithme.flow.commands.Tap
import com.github.aivanovski.testswithme.flow.commands.WaitUntil
import com.github.aivanovski.testswithme.flow.commands.assertion.NotVisibleAssertion
import com.github.aivanovski.testswithme.flow.commands.assertion.VisibleAssertion
import com.github.aivanovski.testswithme.flow.commands.condition.Condition
import com.github.aivanovski.testswithme.flow.commands.condition.NotVisibleCondition
import com.github.aivanovski.testswithme.flow.commands.condition.VisibleCondition

class StepCommandFactory(
    private val interactor: FlowRunnerInteractor
) {

    suspend fun createCommand(
        flow: FlowEntry,
        step: FlowStep
    ): Either<AppException, StepCommand> =
        either {
            when (step) {
                is FlowStep.SendBroadcast -> {
                    val condition = step.condition
                    if (condition != null) {
                        Precondition(
                            condition = condition.type.toCommandCondition(),
                            elements = listOf(condition.element),
                            command = Broadcast(step)
                        )
                    } else {
                        Broadcast(step)
                    }
                }

                is FlowStep.Launch -> Launch(packageName = step.packageName)

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

                is FlowStep.TapOn -> {
                    val condition = step.condition
                    if (condition != null) {
                        Precondition(
                            condition = condition.type.toCommandCondition(),
                            elements = listOf(condition.element),
                            command = Tap(step)
                        )
                    } else {
                        Tap(step)
                    }
                }

                is FlowStep.InputText -> {
                    val condition = step.condition
                    if (condition != null) {
                        Precondition(
                            condition = condition.type.toCommandCondition(),
                            elements = listOf(condition.element),
                            command = InputText(step)
                        )
                    } else {
                        InputText(step)
                    }
                }

                is FlowStep.PressKey -> {
                    val condition = step.condition
                    if (condition != null) {
                        Precondition(
                            condition = condition.type.toCommandCondition(),
                            elements = listOf(condition.element),
                            command = PressKey(step.key)
                        )
                    } else {
                        PressKey(step.key)
                    }
                }

                is FlowStep.WaitUntil -> WaitUntil(step)

                is FlowStep.RunFlow -> {
                    val condition = step.condition
                    val command = createRunFlowCommand(flow, step).bind()

                    if (condition != null) {
                        Precondition(
                            condition = condition.type.toCommandCondition(),
                            elements = listOf(condition.element),
                            command = command
                        )
                    } else {
                        command
                    }
                }
            }
        }

    private suspend fun createRunFlowCommand(
        parent: FlowEntry,
        step: FlowStep.RunFlow
    ): Either<AppException, StepCommand> =
        either {
            val nameOrUid = step.path

            val flow = findFlow(
                parent = parent,
                uidOrPathOrName = nameOrUid
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

    private fun ConditionType.toCommandCondition(): Condition =
        when (this) {
            ConditionType.VISIBLE -> VisibleCondition()
            ConditionType.NOT_VISIBLE -> NotVisibleCondition()
        }

    private suspend fun findFlow(
        parent: FlowEntry,
        uidOrPathOrName: String
    ): Either<AppException, FlowWithSteps> =
        either {
            val getByUidResult = interactor.getFlowByUid(uidOrPathOrName)
            if (getByUidResult.isRight()) {
                return@either getByUidResult.bind()
            }

            interactor.resolveFlowByPathOrName(
                projectUid = parent.projectUid,
                pathOrName = uidOrPathOrName
            ).bind()
                ?: raise(AppException("Unable to find flow: $uidOrPathOrName"))
        }
}