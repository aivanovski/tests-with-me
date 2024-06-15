package com.github.aivanovski.testwithme.android.domain.flow

import android.view.accessibility.AccessibilityNodeInfo
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.domain.FlowInteractor
import com.github.aivanovski.testwithme.flow.driver.Driver
import com.github.aivanovski.testwithme.flow.commands.CompositeStepCommand
import com.github.aivanovski.testwithme.flow.commands.ExecutableStepCommand
import com.github.aivanovski.testwithme.flow.commands.RunFlow
import com.github.aivanovski.testwithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.db.StepEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FlowException
import com.github.aivanovski.testwithme.android.extensions.unwrap
import com.github.aivanovski.testwithme.android.extensions.unwrapError
import com.github.aivanovski.testwithme.flow.commands.StepCommand
import kotlinx.coroutines.delay
import arrow.core.Either

class CommandExecutor(
    private val interactor: FlowInteractor,
    private val driver: Driver<AccessibilityNodeInfo>
) {

    suspend fun execute(
        job: JobEntry,
        flow: FlowEntry,
        stepEntry: StepEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int,
        lifecycleListener: FlowLifecycleListener,
    ): Either<AppException, OnStepFinishedAction> = either {
        lifecycleListener.onStepStarted(flow, command, stepIndex, attemptIndex)

        val result = when {
            command is ExecutableStepCommand<*> -> {
                command.execute(driver)
                    .mapLeft { exception -> FlowException(exception) }
            }

            command is CompositeStepCommand -> {
                executeCompositeCommand(
                    job,
                    command,
                    lifecycleListener
                )
            }

            else -> throw IllegalArgumentException() // TODO: migrate StepCommand to sealed class
        }

        val nextAction = interactor.onStepFinished(job.uid, stepEntry, result).bind()

        lifecycleListener.onStepFinished(flow, command, stepIndex, result)

        when (nextAction) {
            is OnStepFinishedAction.Next -> {
                val updatedJob = job.copy(currentStepUid = nextAction.nextStepUid)
                interactor.updateJob(updatedJob).bind()

                nextAction
            }

            is OnStepFinishedAction.Complete -> {
                lifecycleListener.onFlowFinished(flow, result)

                nextAction
            }

            is OnStepFinishedAction.Stop -> {
                lifecycleListener.onFlowFinished(flow, result)

                if (result.isLeft()) {
                    raise(result.unwrapError())
                } else {
                    nextAction
                }
            }

            else -> {
                nextAction
            }
        }
    }

    private suspend fun executeCompositeCommand(
        job: JobEntry,
        compositeCommand: CompositeStepCommand,
        lifecycleListener: FlowLifecycleListener
    ): Either<AppException, Any> = either {
        var lastResult: Either<AppException, Any>? = null

        if (compositeCommand !is RunFlow) {
            throw IllegalStateException() // TODO: check
        }

        val flow = interactor.getFlowByUid(compositeCommand.flowUid).bind()

        lifecycleListener.onFlowStarted(flow.entry)

        val commands = compositeCommand.getCommands()
        var commandIndex = 0
        while (commandIndex < commands.size) {
            delay(FlowRunner.DELAY_BETWEEN_STEPS)

            val command = commands[commandIndex]

            val stepUid = flow.steps[commandIndex].uid
            val getStepResult = interactor.getStepByUid(stepUid)
            if (getStepResult.isLeft()) {
                lifecycleListener.onFlowFinished(flow.entry, getStepResult)
                raise(getStepResult.unwrapError())
            }

            val executionData = interactor.getExecutionData(
                jobUid = job.uid,
                flowUid = flow.entry.uid,
                stepUid = stepUid
            ).bind()

            val stepEntry = getStepResult.unwrap()

            lifecycleListener.onStepStarted(
                flow.entry,
                command,
                commandIndex,
                executionData.attemptCount
            )

            val commandResult = command.execute(driver)
                .mapLeft { exception -> FlowException(exception) }

            val nextAction = interactor.onStepFinished(job.uid, stepEntry, commandResult)
                .bind()

            lifecycleListener.onStepFinished(
                flow.entry,
                command,
                commandIndex,
                commandResult
            )

            lastResult = commandResult

            when (nextAction) {
                is OnStepFinishedAction.Next -> {
                    commandIndex++
                }

                OnStepFinishedAction.Stop -> {
                    lifecycleListener.onFlowFinished(flow.entry, commandResult)
                    if (commandResult.isLeft()) {
                        raise(commandResult.unwrapError())
                    } else {
                        raise(AppException("Child flow was sopped"))
                    }
                }

                OnStepFinishedAction.Complete -> {
                    lifecycleListener.onFlowFinished(flow.entry, commandResult)
                    break
                }

                OnStepFinishedAction.Retry -> {
                }
            }
        }

        return lastResult ?: raise(AppException("No steps were executed"))
    }
}