package com.github.aivanovski.testwithme.android.domain.flow

import android.view.accessibility.AccessibilityNodeInfo
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.entity.ExecutionResult
import com.github.aivanovski.testwithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testwithme.android.entity.db.FlowEntry
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.db.StepEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FlowException
import com.github.aivanovski.testwithme.entity.exception.ExternalException
import com.github.aivanovski.testwithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testwithme.entity.exception.StepVerificationException
import com.github.aivanovski.testwithme.extensions.unwrapError
import com.github.aivanovski.testwithme.flow.commands.CompositeStepCommand
import com.github.aivanovski.testwithme.flow.commands.ExecutableStepCommand
import com.github.aivanovski.testwithme.flow.commands.RunFlow
import com.github.aivanovski.testwithme.flow.commands.StepCommand
import com.github.aivanovski.testwithme.flow.runner.ExecutionContext
import com.github.aivanovski.testwithme.flow.runner.listener.FlowLifecycleListener
import kotlinx.coroutines.delay

class CommandExecutor(
    private val interactor: FlowRunnerInteractor,
    private val context: ExecutionContext<AccessibilityNodeInfo>,
    private val lifecycleListener: FlowLifecycleListener
) {

    suspend fun executeStandalone(command: StepCommand): Either<AppException, Any> =
        either {
            val result = when {
                command is ExecutableStepCommand<*> -> {
                    command.execute(context)
                        .mapLeft { exception -> FlowException(exception) }
                        .bind()
                }

                else -> throw IllegalArgumentException()
            }

            result
        }

    suspend fun execute(
        isFirstStep: Boolean,
        job: JobEntry,
        flow: FlowEntry,
        stepEntry: StepEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ): Either<AppException, OnStepFinishedAction> =
        either {
            if (isFirstStep) {
                lifecycleListener.onFlowStarted(flow)
            }

            lifecycleListener.onStepStarted(flow, command, stepIndex, attemptIndex)

            val result = when {
                command is ExecutableStepCommand<*> -> {
                    command.execute(context)
                }

                command is CompositeStepCommand -> {
                    executeCompositeCommand(
                        job,
                        command
                    )
                }

                // TODO: migrate StepCommand to sealed class
                else -> throw IllegalArgumentException()
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

                    val updatedJob = job.copy(
                        finishedTimestamp = System.currentTimeMillis(),
                        executionResult = ExecutionResult.SUCCESS
                    )
                    interactor.updateJob(updatedJob).bind()

                    nextAction
                }

                is OnStepFinishedAction.Stop -> {
                    lifecycleListener.onFlowFinished(flow, result)

                    if (result.isLeft()) {
                        val updatedJob = job.copy(
                            finishedTimestamp = System.currentTimeMillis(),
                            executionResult = ExecutionResult.FAILED
                        )
                        interactor.updateJob(updatedJob).bind()

                        raise(AppException(cause = result.unwrapError()))
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
        compositeCommand: CompositeStepCommand
    ): Either<FlowExecutionException, Any> =
        either {
            var lastResult: Either<FlowExecutionException, Any>? = null

            if (compositeCommand !is RunFlow) {
                throw IllegalStateException() // TODO: check
            }

            var flow = interactor.getCachedFlowByUid(compositeCommand.flowUid)
                .mapLeft { exception -> ExternalException(exception) }
                .bind()

            lifecycleListener.onFlowStarted(flow.entry)

            val commands = compositeCommand.getCommands()
            var commandIndex = 0
            while (commandIndex < commands.size) {
                delay(FlowRunner.DELAY_BETWEEN_STEPS)

                flow = interactor.getCachedFlowByUid(compositeCommand.flowUid)
                    .mapLeft { exception -> ExternalException(exception) }
                    .bind()
                val command = commands[commandIndex]

                val stepUid = flow.steps[commandIndex].uid
                val stepEntry = flow.steps[commandIndex]

                val executionData = interactor.getExecutionData(
                    jobUid = job.uid,
                    flowUid = flow.entry.uid,
                    stepUid = stepUid
                )
                    .mapLeft { exception -> ExternalException(exception) }
                    .bind()

                lifecycleListener.onStepStarted(
                    flow.entry,
                    command,
                    commandIndex,
                    executionData.attemptCount
                )

                val commandResult = command.execute(context)

                val nextAction = interactor.onStepFinished(job.uid, stepEntry, commandResult)
                    .mapLeft { exception -> StepVerificationException(exception) }
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
                            raise(FlowExecutionException("Child flow was sopped"))
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

            return lastResult ?: raise(FlowExecutionException("No steps were executed"))
        }
}